package ru.spbstu.shakhmin.obfuscator.cli;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandLineObfuscator {
    void process(@NotNull final String... args);
}
