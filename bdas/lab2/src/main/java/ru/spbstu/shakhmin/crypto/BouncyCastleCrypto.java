package ru.spbstu.shakhmin.crypto;

import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.spbstu.shakhmin.config.AppProperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class BouncyCastleCrypto {

    @NotNull
    private final AppProperties appProperties;

    @NotNull
    private final SecurityStore securityStore;

    @NotNull
    public byte[] encryptData(@NotNull final byte[] data)
            throws CertificateEncodingException, CMSException, IOException
    {
        final var cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();
        cmsEnvelopedDataGenerator.addRecipientInfoGenerator(
                new JceKeyTransRecipientInfoGenerator(securityStore.getCertificate()));
        final var encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
                .setProvider(appProperties.getSecurity().getCert().getProvider())
                .build();
        return cmsEnvelopedDataGenerator.generate(new CMSProcessableByteArray(data), encryptor).getEncoded();
    }

    @NotNull
    public byte[] decryptData(@NotNull final byte[] encryptedData,
                              @NotNull final String keyAlias,
                              @NotNull final String password)
            throws CMSException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException
    {
            final var recipients = new CMSEnvelopedData(encryptedData).getRecipientInfos().getRecipients();
            final var recipientInfo = (KeyTransRecipientInformation) recipients.iterator().next();
            final var recipient = new JceKeyTransEnvelopedRecipient(securityStore.getPrivateKey(keyAlias, password));
            return recipientInfo.getContent(recipient);
    }

    @NotNull
    public byte[] signData(@NotNull final byte[] data,
                           @NotNull final String aliasKey,
                           @NotNull final String password)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            OperatorCreationException, CertificateEncodingException, CMSException, IOException
    {
        final var signingCertificate = securityStore.getCertificate();
        final var certList = new ArrayList<X509Certificate>();
        certList.add(signingCertificate);
        final var cmsGenerator = new CMSSignedDataGenerator();
        final var contentSigner = new JcaContentSignerBuilder(appProperties.getSecurity().getSign().getAlg())
                .build(securityStore.getPrivateKey(aliasKey, password));
        cmsGenerator.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(
                        new JcaDigestCalculatorProviderBuilder()
                                .setProvider(appProperties.getSecurity().getCert().getProvider())
                                .build())
                        .build(contentSigner, signingCertificate));
        cmsGenerator.addCertificates(new JcaCertStore(certList));
        return cmsGenerator.generate(new CMSProcessableByteArray(data), true).getEncoded();
    }

    public boolean verifySignData(@NotNull final byte[] signedData)
            throws IOException, CMSException, OperatorCreationException, CertificateException {
        CMSSignedData cmsSignedData;
        try (final var bIn = new ByteArrayInputStream(signedData)) {
            final var aIn = new ASN1InputStream(bIn);
            cmsSignedData = new CMSSignedData(ContentInfo.getInstance(aIn.readObject()));
            aIn.close();
        }
        final var certs = cmsSignedData.getCertificates();
        final var signers = cmsSignedData.getSignerInfos().getSigners();
        boolean verifyResult = true;
        for (final var signer : signers) {
            verifyResult = signer.verify(
                    new JcaSimpleSignerInfoVerifierBuilder()
                            .build((X509CertificateHolder) certs.getMatches(signer.getSID()).iterator().next()));
        }
        return verifyResult;
    }
}
