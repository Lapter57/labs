package ru.spbstu.shakhmin.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spbstu.shakhmin.crypto.SecurityStore;

import java.security.Security;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    @NotNull
    private final AppProperties appProperties;

    @SneakyThrows
    @Bean
    public SecurityStore getSecurityStore() {
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
        return new SecurityStore(appProperties);
    }
}
