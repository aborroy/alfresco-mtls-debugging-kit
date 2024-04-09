package org.alfresco.test;

import org.alfresco.crypto.CryptoUtils;
import org.alfresco.crypto.Endpoint;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class App {

    static String ksLocation = "/Users/angel.fernandoborroy/Downloads/zz-mtls/alfresco-mtls-debugging-kit/docker/keystores/ecdsa/alfresco.pkcs12";
    static String tsLocation = "/Users/angel.fernandoborroy/Downloads/zz-mtls/alfresco-mtls-debugging-kit/docker/keystores/ecdsa/truststore.pkcs12";

    public static void main(String... args) throws Exception {

        Endpoint endpoint = CryptoUtils.getTlsEndpointParameters(
                "localhost", 8983,
                "PKCS12", ksLocation, "keystore".toCharArray(),
                "PKCS12", tsLocation, "truststore".toCharArray());
        endpoint.getTrustedCertificates().forEach(trusted -> {
            System.out.println(trusted.getName());
        });
        System.out.println(endpoint.getTlsProcotol());

        // Set up SSL properties
        System.setProperty("javax.net.ssl.keyStore", ksLocation);
        System.setProperty("javax.net.ssl.keyStorePassword", "keystore");
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
        System.setProperty("javax.net.ssl.trustStore", tsLocation);
        System.setProperty("javax.net.ssl.trustStorePassword", "truststore");
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

        // Create URL object
        URL endpointUrl = new URL("https://localhost:8983/solr");

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        // Open HTTPS connection
        HttpsURLConnection connection = (HttpsURLConnection) endpointUrl.openConnection();

        // Set request method
        connection.setRequestMethod("GET");

        // Get response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Get response headers
        Map<String, List<String>> headers = connection.getHeaderFields();
        System.out.println("-------------->" + connection.getHeaderField("Server"));
        System.out.println("Response Headers:");
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            List<String> headerValues = entry.getValue();
            if (headerName != null) {
                for (String value : headerValues) {
                    System.out.println(headerName + ": " + value);
                }
            }
        }

        // Read response body
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBody.append(line);
        }
        reader.close();

        // Print response body
        System.out.println("Response Body:");
        System.out.println(responseBody.toString());

        // Close connection
        connection.disconnect();

    }
}
