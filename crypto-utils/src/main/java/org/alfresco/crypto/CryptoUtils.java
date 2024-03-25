package org.alfresco.crypto;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

/**
 * Utility class for cryptographic operations and TLS endpoint parameter retrieval.
 */
public class CryptoUtils {

    /**
     * Map containing human-readable names for Extended Key Usage OIDs.
     */
    public static final Map<String, String> EXTENDED_KEY_USAGE_NAMES = new HashMap<>();
    static {
        EXTENDED_KEY_USAGE_NAMES.put("1.3.6.1.5.5.7.3.1", "Server Authentication");
        EXTENDED_KEY_USAGE_NAMES.put("1.3.6.1.5.5.7.3.2", "Client Authentication");
        EXTENDED_KEY_USAGE_NAMES.put("1.3.6.1.5.5.7.3.3", "Code Signing");
        EXTENDED_KEY_USAGE_NAMES.put("1.3.6.1.5.5.7.3.4", "Email Protection");
        EXTENDED_KEY_USAGE_NAMES.put("1.3.6.1.5.5.7.3.8", "Time Stamping");
        EXTENDED_KEY_USAGE_NAMES.put("1.3.6.1.5.5.7.3.9", "OCSP Signing");
    }

    private static final String TRUST = "TRUST";
    private static final String KEY = "KEY";
    private static final String OK = "OK";
    private static final String ERROR = "ERROR";

    /**
     * Retrieves TLS endpoint parameters such as supported protocols and trusted CA details.
     *
     * @param host The hostname of the endpoint.
     * @param port The port of the endpoint.
     * @return An object containing TLS endpoint parameters.
     */
    public static Endpoint getTlsEndpointParameters(String host, int port) {
        Endpoint tlsEndpoint = new Endpoint();
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
                String[] supportedProtocols = socket.getEnabledProtocols();
                tlsEndpoint.setSupportedProcotols(Arrays.asList(supportedProtocols));
                socket.startHandshake();
                SSLSession session = socket.getSession();
                List<TrustedCertificate> trustedCertificates = new ArrayList<>();
                Arrays.stream(session.getPeerCertificates()).forEach(certificate -> {
                    TrustedCertificate trustedCertificate = new TrustedCertificate();
                    X509Certificate x509certificate = ((X509Certificate) certificate);
                    trustedCertificate.setName(x509certificate.getSubjectX500Principal().getName());
                    trustedCertificate.setExpiration(x509certificate.getNotAfter());
                    trustedCertificates.add(trustedCertificate);
                });
                tlsEndpoint.setTrustedCertificates(trustedCertificates);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return tlsEndpoint;
    }

    /**
     * Verifies keystore or truststore properties.
     *
     * @param type     Keystore type (JKS, JCEKS, PKCS12).
     * @param location File path of the keystore.
     * @param password Password for the keystore and the keys.
     * @param aliases  Expected aliases to be part of the keystore.
     * @return Verification information for the keystore.
     */
    public static KeystoreInfo verifyKeyStore(String type, String location, char[] password, String[] aliases) {

        KeystoreInfo keystoreInfo = new KeystoreInfo();

        try {

            KeyStore ks = KeyStore.getInstance(type);
            ks.load(Files.newInputStream(Paths.get(location)), password);

            List<AliasExistence> aliasExistenceList = new ArrayList<>();
            for (String alias : aliases) {
                AliasExistence aliasExistence = new AliasExistence();
                aliasExistence.setAlias(alias);
                aliasExistence.setExists(ks.containsAlias(alias));
                aliasExistenceList.add(aliasExistence);
            }
            keystoreInfo.setAliasExistenceList(aliasExistenceList);

            Enumeration<String> aliasesKS = ks.aliases();
            List<AliasDetails> aliasDetailsList = new ArrayList<>();
            while (aliasesKS.hasMoreElements()) {
                String alias = aliasesKS.nextElement();
                Key aliasKey = ks.getKey(alias, password);
                X509Certificate aliasCertificate = (X509Certificate) ks.getCertificate(alias);
                AliasDetails aliasDetails = new AliasDetails();
                aliasDetails.setAlias(alias);
                aliasDetails.setType(aliasKey == null ? TRUST : KEY);
                aliasDetails.setSubject(aliasCertificate.getSubjectX500Principal().getName());
                aliasDetails.setIssuer(aliasCertificate.getIssuerX500Principal().getName());
                aliasDetails.setExpiration(aliasCertificate.getNotAfter());
                aliasDetails.setAlgorithm(aliasCertificate.getSigAlgOID() + " - " + aliasCertificate.getSigAlgName());
                aliasDetails.setSize(CryptoUtils.getPublicKeySize(aliasCertificate.getPublicKey()) + " bits");
                if (aliasCertificate.getExtendedKeyUsage() != null) {
                    List<KeyUsage> usageList = new ArrayList<>();
                    for (String usage : aliasCertificate.getExtendedKeyUsage()) {
                        KeyUsage keyUsage = new KeyUsage();
                        keyUsage.setOid(usage);
                        keyUsage.setName(EXTENDED_KEY_USAGE_NAMES.getOrDefault(usage, "Unknown Extended Key Usage"));
                        usageList.add(keyUsage);
                    }
                    aliasDetails.setUsages(usageList);
                }
                aliasDetailsList.add(aliasDetails);
            }
            keystoreInfo.setAliasDetailsList(aliasDetailsList);

            keystoreInfo.setStatus(OK);

        } catch (NoSuchFileException nsfe) {
            nsfe.printStackTrace(System.err);
            keystoreInfo.setStatus(ERROR + "File " + location + " does not exist");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            String error = (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            if (error.contains("DerInputStream")) {
                error = "The type of the keystore is not " + type + ". " + error;
            }
            keystoreInfo.setStatus(ERROR + error);
        }

        return keystoreInfo;

    }

    /**
     * Retrieves the size of the public key.
     *
     * @param publicKey The public key for which to determine the size.
     * @return The size of the public key in bits.
     */
    public static int getPublicKeySize(PublicKey publicKey) {
        String algorithm = publicKey.getAlgorithm();
        if (algorithm.equals("RSA")) {
            return ((RSAPublicKey) publicKey).getModulus().bitLength();
        }
        return 0; // Unsupported algorithm
    }

}
