package ru.spbstu.shakhmin;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.spbstu.shakhmin.cli.CommandLineProcessor;
import ru.spbstu.shakhmin.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@RequiredArgsConstructor
public class Main implements CommandLineRunner {

    @NotNull
    private final CommandLineProcessor clProcessor;

    public static void main(final String[] args) {
        final var app = new SpringApplication(Main.class);
        app.run(args);
    }

    @Override
    public void run(final String... args) {
        clProcessor.process(args);
    }
}
