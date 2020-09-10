package ru.spbstu.shakhmin.obfuscator.cli;

@FunctionalInterface
public interface CommandLineObfuscator {
    void process(String... args);
}
