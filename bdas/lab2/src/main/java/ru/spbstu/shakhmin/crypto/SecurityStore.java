package ru.spbstu.shakhmin.crypto;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.spbstu.shakhmin.config.AppProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Component
public class SecurityStore {

    @NotNull
    private final X509Certificate certificate;

    @NotNull
    private final KeyStore keyStore;

    public SecurityStore(@NotNull final AppProperties appProperties)
            throws CertificateException, NoSuchProviderException, IOException,
            KeyStoreException, NoSuchAlgorithmException
    {
        final var classLoader = getClass().getClassLoader();
        final var certConfig = appProperties.getSecurity().getCert();
        final var keyStoreConfig = appProperties.getSecurity().getKeyStore();
        final var certFactory = CertificateFactory.getInstance(certConfig.getType(), certConfig.getProvider());
        this.certificate = (X509Certificate) certFactory.generateCertificate(classLoader.getResourceAsStream(certConfig.getPath()));
        this.keyStore = KeyStore.getInstance(keyStoreConfig.getType());
        this.keyStore.load(classLoader.getResourceAsStream(keyStoreConfig.getPath()), keyStoreConfig.getPassword().toCharArray());
    }

    @NotNull
    public X509Certificate getCertificate() {
        return certificate;
    }

    @NotNull
    public PrivateKey getPrivateKey(@NotNull final String keyAlias,
                                    @NotNull final String password)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException
    {
        return (PrivateKey) keyStore.getKey(keyAlias, password.toCharArray());
    }
}
