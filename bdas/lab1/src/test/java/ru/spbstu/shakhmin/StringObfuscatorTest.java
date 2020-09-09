package ru.spbstu.shakhmin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StringObfuscatorTest {

    private static final String TEST_STRING = "The goal of this document is to provide comprehensive reference documentation";

    private final Obfuscator stringObfuscator = StringObfuscator.create();

    private String obfuscatedSource;


    @BeforeEach
    public void init() {
        obfuscatedSource = stringObfuscator.obfuscate(TEST_STRING);
    }

    @Test
    public void obfuscate() {
        assertEquals(TEST_STRING.length(), obfuscatedSource.length());
        assertNotEquals(TEST_STRING, obfuscatedSource);
    }

    @Test
    public void unobfuscate() {
        assertEquals(TEST_STRING, stringObfuscator.unobfuscate(obfuscatedSource));
    }
}