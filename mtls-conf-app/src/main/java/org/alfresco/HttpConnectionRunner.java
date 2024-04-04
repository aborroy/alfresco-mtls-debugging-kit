package org.alfresco;

import javax.net.ssl.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

public class HttpConnectionRunner {

    public static void testConnection(
            String host, int port, String contextPath,
            String typeKs, String locationKs, char[] passwordKs,
            String typeTs, String locationTs, char[] passwordTs) {

        try {

            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory kmFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            KeyStore ks = KeyStore.getInstance(typeKs);
            ks.load(Files.newInputStream(Paths.get(locationKs)), passwordKs);
            kmFactory.init(ks, passwordKs);

            KeyStore ts = KeyStore.getInstance(typeTs);
            ts.load(Files.newInputStream(Paths.get(locationTs)), passwordTs);
            tmFactory.init(ts);

            sslContext.init(kmFactory.getKeyManagers(), tmFactory.getTrustManagers(), null);
            SSLSocketFactory factory = sslContext.getSocketFactory();

            URL url = new URL("https", host, port, contextPath);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(factory);
            connection.setHostnameVerifier((hostname, session) -> true);
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("Expecting response code 200, but got: " + connection.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }

}
