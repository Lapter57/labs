package ru.spbstu.shakhmin;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.spbstu.shakhmin.obfuscator.cli.CommandLineXMLObfuscator;

@SpringBootApplication
@RequiredArgsConstructor
public class Main implements CommandLineRunner {

    @NotNull
    private final CommandLineXMLObfuscator clXMLObfuscator;

    public static void main(final String[] args) {
        final var app = new SpringApplication(Main.class);
        app.run(args);
    }

    @Override
    public void run(final String... args) {
        clXMLObfuscator.process(args);
    }
}
