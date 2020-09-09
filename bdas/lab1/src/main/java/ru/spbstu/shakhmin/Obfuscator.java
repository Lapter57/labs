package ru.spbstu.shakhmin;

import org.jetbrains.annotations.NotNull;

public interface Obfuscator {

    @NotNull
    String obfuscate(@NotNull final String source);

    @NotNull
    String unobfuscate(@NotNull final String obfuscatedSource);
}
