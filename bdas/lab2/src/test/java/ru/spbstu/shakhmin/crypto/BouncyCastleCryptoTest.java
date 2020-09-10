package ru.spbstu.shakhmin.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spbstu.shakhmin.config.AppProperties;

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

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

    @BeforeAll
    public static void init() {
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void checkConfiguration() throws NoSuchAlgorithmException {
        assertNotEquals(128, Cipher.getMaxAllowedKeyLength("AES"));
    }

    @Test
    public void encryptData() throws Exception {
        assertNotEquals(SECRETE_MSG, new String(getEncryptedData()));
    }

    @Test
    public void decryptData() throws Exception {
        assertEquals(SECRETE_MSG, new String(getDecryptedData()));
    }

    @Test
    public void signData() throws Exception {
        assertNotEquals(getDecryptedData(), getSignedData());
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
        return bouncyCastleCrypto.signData(getDecryptedData(), KEY_ALIAS, KEY_PASSWORD);
    }
}