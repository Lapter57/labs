package ru.spbstu.shakhmin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @NotNull
    private final Security security = new Security();

    @Getter
    @Setter
    public static final class Security {

        @NotNull
        private final CertConfig cert = new CertConfig();

        @NotNull
        private final KeyStoreConfig keyStore = new KeyStoreConfig();

        @NotNull
        private final SignConfig sign = new SignConfig();

        @Getter
        @Setter
        public static final class CertConfig {

            @NotBlank
            private String path;

            @NotBlank
            private String type;

            @NotBlank
            private String provider;
        }

        @Getter
        @Setter
        public static final class KeyStoreConfig {

            @NotBlank
            private String path;

            @NotBlank
            private String type;

            @NotBlank
            private String password;
        }

        @Getter
        @Setter
        public static final class SignConfig {

            @NotBlank
            private String alg;
        }
    }
}