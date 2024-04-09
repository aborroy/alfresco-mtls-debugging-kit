package org.alfresco;

import javax.net.ssl.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;

/**
 * Utility class for testing HTTPS connections with custom SSL configuration.
 */
public class HttpConnectionRunner {

    /**
     * Tests an HTTPS connection with custom SSL configuration.
     *
     * @param host         The hostname of the server.
     * @param port         The port number of the server.
     * @param contextPath  The context path of the URL.
     * @param typeKs       The type of KeyStore (e.g., "JKS", "PKCS12").
     * @param locationKs   The file path to the KeyStore.
     * @param passwordKs   The password for the KeyStore.
     * @param typeTs       The type of TrustStore (e.g., "JKS", "PKCS12").
     * @param locationTs   The file path to the TrustStore.
     * @param passwordTs   The password for the TrustStore.
     */
    public static void testConnection(
            String host, int port, String contextPath,
            String typeKs, String locationKs, char[] passwordKs,
            String typeTs, String locationTs, char[] passwordTs) {

        try {
            // Setup SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory kmFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Load KeyStore
            KeyStore ks = KeyStore.getInstance(typeKs);
            ks.load(Files.newInputStream(Paths.get(locationKs)), passwordKs);
            kmFactory.init(ks, passwordKs);

            // Load TrustStore
            KeyStore ts = KeyStore.getInstance(typeTs);
            ts.load(Files.newInputStream(Paths.get(locationTs)), passwordTs);
            tmFactory.init(ts);

            // Initialize SSL context
            sslContext.init(kmFactory.getKeyManagers(), tmFactory.getTrustManagers(), null);
            SSLSocketFactory factory = sslContext.getSocketFactory();

            // Open HTTPS connection
            URL url = new URL("https", host, port, contextPath);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(factory);
            connection.setHostnameVerifier((hostname, session) -> true);
            connection.connect();

            // Check response code
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("Expecting response code 200, but got: " + connection.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
