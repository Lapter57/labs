package ru.spbstu.shakhmin.obfuscator;

import org.jetbrains.annotations.NotNull;

public interface Obfuscator {

    @NotNull
    String obfuscate(@NotNull final String source);

    @NotNull
    String unobfuscate(@NotNull final String obfuscatedSource);

    default String obfuscateString(@NotNull final String string,
                                   @NotNull final String key) {
        final char[] obfuscatedSource = new char[string.length()];
        for (int i = 0; i < string.length(); i++) {
            obfuscatedSource[i] = (char) (string.charAt(i) + key.charAt(i % key.length()));
        }
        return new String(obfuscatedSource);
    }

    default String unobfuscateString(@NotNull final String obfuscatedString,
                                     @NotNull final String key) {
        final char[] source = new char[obfuscatedString.length()];
        for (int i = 0; i < obfuscatedString.length(); i++) {
            source[i] = (char) (obfuscatedString.charAt(i) - key.charAt(i % key.length()));
        }
        return new String(source);
    }
}
