package org.alfresco.solr.action;

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

    /**
     * Retrieves TLS endpoint parameters such as supported protocols and trusted CA details.
     *
     * @param host The hostname of the endpoint.
     * @param port The port of the endpoint.
     * @return A map containing TLS endpoint parameters.
     */
    public static Map<String, Object> getTlsEndpointParameters(String host, int port) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
                String[] supportedProtocols = socket.getEnabledProtocols();
                parameters.put("protocol", Arrays.asList(supportedProtocols));
                socket.startHandshake();
                SSLSession session = socket.getSession();
                Map<String, Object> certificates = new LinkedHashMap<>();
                Arrays.stream(session.getPeerCertificates()).forEach(certificate -> {
                    Map<String, String> certificateEntry = new LinkedHashMap<>();
                    X509Certificate x509certificate = ((X509Certificate) certificate);
                    certificateEntry.put("ca-name", x509certificate.getSubjectX500Principal().getName());
                    certificateEntry.put("expiration", x509certificate.getNotAfter().toString());
                    certificates.put(x509certificate.getSerialNumber().toString(), certificateEntry);
                });
                parameters.put("trusted-ca", certificates);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return parameters;
    }

    /**
     * Verifies keystore or truststore properties.
     *
     * @param type     Keystore type (JKS, JCEKS, PKCS12).
     * @param location File path of the keystore.
     * @param password Password for the keystore and the keys.
     * @param aliases  Expected aliases to be part of the keystore.
     * @return Verification information in a structure of Strings and Maps.
     */
    public static Map<String, Object> verifyKeyStore(String type, String location, char[] password, String[] aliases) {

        Map<String, Object> status = new LinkedHashMap<>();
        try {

            KeyStore ks = KeyStore.getInstance(type);
            ks.load(Files.newInputStream(Paths.get(location)), password);

            Map<String, Boolean> aliasesVerify = new LinkedHashMap<>();
            for (String alias : aliases) {
                aliasesVerify.put(alias, ks.containsAlias(alias));
            }
            status.put("aliases-properties", aliasesVerify);

            Enumeration<String> aliasesKS = ks.aliases();
            Map<String, Object> aliasesKSList = new LinkedHashMap<>();
            while (aliasesKS.hasMoreElements()) {
                String alias = aliasesKS.nextElement();
                Key aliasKey = ks.getKey(alias, password);
                Map<String, Object> aliasDetails = new LinkedHashMap<>();
                X509Certificate aliasCertificate = (X509Certificate) ks.getCertificate(alias);
                aliasDetails.put("alias", alias);
                aliasDetails.put("type", aliasKey == null ? HttpClientAction.TRUST : HttpClientAction.KEY);
                aliasDetails.put("subject", aliasCertificate.getSubjectX500Principal().getName());
                aliasDetails.put("issuer", aliasCertificate.getIssuerX500Principal().getName());
                aliasDetails.put("expiration", aliasCertificate.getNotAfter().toString());
                aliasDetails.put("algorithm", aliasCertificate.getSigAlgOID() + " - " + aliasCertificate.getSigAlgName());
                aliasDetails.put("size", CryptoUtils.getPublicKeySize(aliasCertificate.getPublicKey()) + " bits");
                if (aliasCertificate.getExtendedKeyUsage() != null) {
                    List<Object> usages = new ArrayList<>();
                    for (String usage : aliasCertificate.getExtendedKeyUsage()) {
                        Map<String, String> usageDetail = new LinkedHashMap<>();
                        usageDetail.put("oid", usage);
                        usageDetail.put("name", EXTENDED_KEY_USAGE_NAMES.getOrDefault(usage, "Unknown Extended Key Usage"));
                        usages.add(usageDetail);
                    }
                    aliasDetails.put("usage", usages);
                }
                aliasesKSList.put(alias, aliasDetails);
            }
            status.put("aliases-keystore", aliasesKSList);

            status.put("keystore", HttpClientAction.OK);

        } catch (NoSuchFileException nsfe) {
            nsfe.printStackTrace(System.err);
            status.put("keystore", HttpClientAction.ERROR + "File " + location + " does not exist");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            String error = (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            if (error.contains("DerInputStream")) {
                error = "The type of the keystore is not " + type + ". " + error;
            }
            status.put("keystore", HttpClientAction.ERROR + error);
        }

        return status;

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
