package ru.spbstu.shakhmin.obfuscator;

@FunctionalInterface
public interface CommandLineObfuscator {
    void process(String... args);
}
