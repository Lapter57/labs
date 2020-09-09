package ru.spbstu.shakhmin;

import org.jetbrains.annotations.NotNull;

public class StringObfuscator implements Obfuscator {

    private static final String DEFAULT_KEY = "XlzUUbhmC09smbIbnriH8WJaivuxOqAE";

    private final String key;

    public StringObfuscator(@NotNull final String key) {
        this.key = key;
    }

    public static StringObfuscator create() {
        return new StringObfuscator(DEFAULT_KEY);
    }

    @NotNull
    @Override
    public String obfuscate(@NotNull final String source) {
        final char[] obfuscatedSource = new char[source.length()];
        for (int i = 0; i < source.length(); i++) {
            obfuscatedSource[i] = (char) (source.charAt(i) + key.charAt(i % key.length()));
        }
        return new String(obfuscatedSource);
    }

    @NotNull
    @Override
    public String unobfuscate(@NotNull final String obfuscatedSource) {
        final char[] source = new char[obfuscatedSource.length()];
        for (int i = 0; i < obfuscatedSource.length(); i++) {
            source[i] = (char) (obfuscatedSource.charAt(i) - key.charAt(i % key.length()));
        }
        return new String(source);
    }
}
