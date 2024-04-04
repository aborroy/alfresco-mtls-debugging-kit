package org.alfresco;

import org.alfresco.crypto.CryptoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

    // Bad port          java.net.ConnectException: Connection refused
    // Bad server        java.net.ConnectException: Operation timed out
    //                   java.net.UnknownHostException:
    // Bad KS type       java.io.IOException: Invalid keystore format
    // Bad pass          java.io.IOException: keystore password was incorrect
    // File missed       java.nio.file.NoSuchFileException:
    // Wrong keystore    javax.net.ssl.SSLHandshakeException: Received fatal alert: bad_certificate
    // Wrong truststore  sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

    private void analyzeErrors(ByteArrayOutputStream errorStream, String element) {

        String error = errorStream.toString();
        if (!error.isEmpty()) {

            System.out.println("--");

            System.out.println("ERRORS for " + element + ": ");
            if (error.contains("java.net.ConnectException: Connection refused")) {
                System.out.println("Current port setting (" + port + ") seems to be wrong. \n" +
                        "Verify if the port is open in server '" + host + "' or change the value to a different port.");
            }
            if (error.contains("java.net.ConnectException: Operation timed out") || error.contains("java.net.UnknownHostException:")) {
                System.out.println("Current server setting (" + host + ") seems to be wrong. \n" +
                        "Verify if you have access to server '" + host + "' or change the value to a different host name.");
            }
            if (error.contains("java.io.IOException: Invalid keystore format")) {
                System.out.println("Current keystore type setting (" + (element.equals ("KEYSTORE") ? keystoreType : truststoreType) + ") seems to be wrong. \n" +
                        "Verify if keystore located in " + (element.equals ("KEYSTORE") ? keystoreLocation : truststoreLocation) + " has this format. \n" +
                        "You may use the following command: keytool -list -keystore "+ (element.equals ("KEYSTORE") ? keystoreLocation : truststoreLocation));
            }
            if (error.contains("java.io.IOException: keystore password was incorrect")) {
                System.out.println("Current keystore password setting (" + (element.equals ("KEYSTORE") ? keystorePassword : truststorePassword) + ") seems to be wrong. \n" +
                        "Verify if keystore located in " + (element.equals ("KEYSTORE") ? keystoreLocation : truststoreLocation) + " can be opened with this password.");
            }
            if (error.contains("java.nio.file.NoSuchFileException:")) {
                System.out.println("Current keystore location setting (" + (element.equals ("KEYSTORE") ? keystoreLocation : truststoreLocation) + ") seems to be wrong. \n" +
                        "Verify if keystore os located in " + (element.equals ("KEYSTORE") ? keystoreLocation : truststoreLocation) + " or change the property value.");
            }
            if (error.contains("javax.net.ssl.SSLHandshakeException: Received fatal alert: bad_certificate")) {
                System.out.println("Current keystore seems to be wrong. It does not include KEY certificates accepted by the endpoint.");
            }
            if (error.contains("sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target")) {
                System.out.println("Current truststore seems to be wrong. It does not include TRUST certificates provided by the endpoint.");
            }
            System.out.println("ERRORS DETAIL: ");
            System.out.println(error);
            errorStream.reset();
        }

    }

    @Override
    public void run(String... args) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outputStream));

        CryptoUtils.getTlsEndpointParameters(
                host, port,
                keystoreType, keystoreLocation, keystorePassword.toCharArray(),
                truststoreType, truststoreLocation, truststorePassword.toCharArray());

        analyzeErrors(outputStream, "ENDPOINT");

        HttpConnectionRunner.testConnection(
                host, port, context,
                keystoreType, keystoreLocation, keystorePassword.toCharArray(),
                truststoreType, truststoreLocation, truststorePassword.toCharArray());

        analyzeErrors(outputStream, "CONNECTION");

        CryptoUtils.verifyKeyStore(keystoreType, keystoreLocation, keystorePassword.toCharArray(), new String[]{});

        analyzeErrors(outputStream, "KEYSTORE");

        CryptoUtils.verifyKeyStore(truststoreType, truststoreLocation, truststorePassword.toCharArray(), new String[]{});

        analyzeErrors(outputStream, "TRUSTSTORE");

    }

}
