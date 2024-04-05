package org.alfresco;

import org.alfresco.crypto.CryptoUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for the command-line application that tests mTLS connections and performs error analysis.
 */
public class Application implements CommandLineRunner {

    @Value("${endpoint.host}")
    String host;

    @Value("${endpoint.port}")
    int port;

    @Value("${endpoint.context}")
    String context;

    @Value("${keystore.type}")
    String keystoreType;

    @Value("${keystore.location}")
    String keystoreLocation;

    @Value("${keystore.password}")
    String keystorePassword;

    @Value("${truststore.type}")
    String truststoreType;

    @Value("${truststore.location}")
    String truststoreLocation;

    @Value("${truststore.password}")
    String truststorePassword;

    public static void main(String... args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Analyzes errors and prints error messages based on the error stream content.
     *
     * @param errorStream Error stream containing error messages.
     * @param element     Element to analyze errors for (e.g., "ENDPOINT", "CONNECTION", "KEYSTORE", "TRUSTSTORE").
     * @param params      Key and value map for ErrorCatalog error messages named parameters.
     */
    private void analyzeErrors(ByteArrayOutputStream errorStream, String element, Map<String, Object> params) {

        String error = errorStream.toString();
        if (!error.isEmpty()) {
            System.out.println("--");
            System.out.println("ERRORS for " + element + ": ");
            ErrorCatalog.ERRORS.keySet().forEach(message -> {
                if (error.contains(message)) {
                    System.out.println(StringSubstitutor.replace(ErrorCatalog.ERRORS.get(message), params, "${", "}"));
                }
            });
            System.out.println("ERRORS DETAIL: ");
            System.out.println(error);
            errorStream.reset();
        }

    }

    @Override
    public void run(String... args) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outputStream));

        Map<String, Object> params = new HashMap<>();
        params.put("host", host);
        params.put("port", port);

        CryptoUtils.getTlsEndpointParameters(
                host, port,
                keystoreType, keystoreLocation, keystorePassword.toCharArray(),
                truststoreType, truststoreLocation, truststorePassword.toCharArray());

        analyzeErrors(outputStream, "ENDPOINT", params);

        HttpConnectionRunner.testConnection(
                host, port, context,
                keystoreType, keystoreLocation, keystorePassword.toCharArray(),
                truststoreType, truststoreLocation, truststorePassword.toCharArray());

        analyzeErrors(outputStream, "CONNECTION", params);

        CryptoUtils.verifyKeyStore(keystoreType, keystoreLocation, keystorePassword.toCharArray(), new String[]{});

        params.put("keystore_type", keystoreType);
        params.put("keystore_location", keystoreLocation);
        params.put("keystore_password", keystorePassword);
        analyzeErrors(outputStream, "KEYSTORE", params);

        CryptoUtils.verifyKeyStore(truststoreType, truststoreLocation, truststorePassword.toCharArray(), new String[]{});

        params.put("keystore_type", truststoreType);
        params.put("keystore_location", truststoreLocation);
        params.put("keystore_password", truststorePassword);
        analyzeErrors(outputStream, "TRUSTSTORE", params);

    }

}
