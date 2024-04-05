package org.alfresco;

import java.util.Map;

/**
 * Interface representing a catalog of errors and their corresponding messages.
 */
public interface ErrorCatalog {

    /**
     * A map containing error codes as keys and corresponding error messages as values.
     * The error codes are the full class names of exceptions plus exception message.
     */
    Map<String, String> ERRORS = Map.of(
            "java.net.ConnectException: Connection refused",
                "Current port setting '${port}' seems to be wrong. \n" +
                    "Verify if the port is open in server '${host}' or change the value to a different port.",
            "java.net.ConnectException: Operation timed out",
                "Current server setting '${host}' seems to be wrong. \n" +
                    "Verify if you have access to server ''${host}'' or change the value to a different host name.",
            "java.net.UnknownHostException:",
                "Current server setting '${host}' seems to be wrong. \n" +
                    "Verify if you have access to server ''${host}'' or change the value to a different host name.",
            "java.io.IOException: Invalid keystore format",
                "Current keystore type setting '${keystore_type}' seems to be wrong. \n" +
                    "Verify if keystore located in '${keystore_location}' has this format. \n" +
                    "You may use the following command: keytool -list -keystore ${keystore_location}",
            "java.io.IOException: keystore password was incorrect",
                "Current keystore password setting '${keystore_password}' seems to be wrong. \n" +
                    "Verify if keystore located in '${keystore_location}' can be opened with this password.",
            "java.nio.file.NoSuchFileException:",
                "Current keystore location setting '${keystore_location}' seems to be wrong. \n" +
                    "Verify if keystore os located in '${keystore_location}' or change the property value.",
            "javax.net.ssl.SSLHandshakeException: Received fatal alert: bad_certificate",
                "Current keystore seems to be wrong. It does not include KEY certificates accepted by the endpoint.",
            "sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target",
                "Current truststore seems to be wrong. It does not include TRUST certificates provided by the endpoint."
    );

}
