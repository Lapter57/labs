package ru.spbstu.shakhmin.crypto;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spbstu.shakhmin.config.AppProperties;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = BouncyCastleCryptoTestConfiguration.class)
@EnableConfigurationProperties(value = AppProperties.class)
class BouncyCastleCryptoTest {

    private static final String SECRETE_MSG = "My password is 123456Seven";
    private static final String KEY_ALIAS = "baeldung";
    private static final String KEY_PASSWORD = "password";

    @Autowired
    private BouncyCastleCrypto bouncyCastleCrypto;

    @Test
    public void checkConfiguration() throws NoSuchAlgorithmException {
        assertNotEquals(128, Cipher.getMaxAllowedKeyLength("AES"));
    }

    @Test
    public void encryptData() throws Exception {
        assertNotEquals(SECRETE_MSG, new String(getEncryptedData(), StandardCharsets.UTF_8));
    }

    @Test
    public void decryptData() throws Exception {
        assertEquals(SECRETE_MSG, new String(getDecryptedData(), StandardCharsets.UTF_8));
    }

    @Test
    public void signData() throws Exception {
        assertNotEquals(SECRETE_MSG.getBytes(), getSignedData());
    }

    @Test
    public void verifyData() throws Exception {
        assertTrue(bouncyCastleCrypto.verifySignData(getSignedData()));
    }

    @NotNull
    private byte[] getEncryptedData() throws Exception {
        return bouncyCastleCrypto.encryptData(SECRETE_MSG.getBytes());
    }

    @NotNull
    private byte[] getDecryptedData() throws Exception {
        return bouncyCastleCrypto.decryptData(getEncryptedData(), KEY_ALIAS, KEY_PASSWORD);
    }

    @NotNull
    private byte[] getSignedData() throws Exception {
        return bouncyCastleCrypto.signData(SECRETE_MSG.getBytes(), KEY_ALIAS, KEY_PASSWORD);
    }
}