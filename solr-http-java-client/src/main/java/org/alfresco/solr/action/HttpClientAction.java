package org.alfresco.solr.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.crypto.CryptoUtils;
import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.SolrKeyResourceLoader;
import org.alfresco.solr.client.Acl;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.SOLRAPIClientFactory;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.response.SolrQueryResponse;

import java.util.*;

/**
 * Utility class for performing actions related to HTTP clients.
 */
public class HttpClientAction {

    public static final String OK = "OK";
    public static final String ERROR = "ERROR: ";

    /**
     * Adds repository details to the SolrQueryResponse.
     *
     * @param rsp            The SolrQueryResponse object to which repository details will be added.
     * @param coreProperties Properties of the Solr core.
     * @param loader         Solr resource loader.
     */
    public static void addRepoDetails(SolrQueryResponse rsp, Properties coreProperties, SolrResourceLoader loader) {

        Map<String, Object> alfresco = new LinkedHashMap<>();

        String secureCommsType = coreProperties.getProperty("alfresco.secureComms", "none");

        Map<String, String> alfrescoProperties = new LinkedHashMap<>();
        alfrescoProperties.put("alfresco.host", coreProperties.getProperty("alfresco.host", "localhost"));
        alfrescoProperties.put("alfresco.port", coreProperties.getProperty("alfresco.port", "8080"));
        alfrescoProperties.put("alfresco.port.ssl", coreProperties.getProperty("alfresco.port.ssl", "8080"));
        alfrescoProperties.put("alfresco.baseUrl", coreProperties.getProperty("alfresco.baseUrl", "/alfresco"));
        alfrescoProperties.put("alfresco.secureComms", secureCommsType);
        alfresco.put("properties", alfrescoProperties);

        Map<String, Object> alfrescoVerifications = new LinkedHashMap<>();
        if (HttpClientFactory.SecureCommsType.getType(secureCommsType) == HttpClientFactory.SecureCommsType.HTTPS) {
            alfrescoVerifications.put("alfresco.solr.api.url",
                    "https://" + alfrescoProperties.get("alfresco.host") + ":" +
                            alfrescoProperties.get("alfresco.port.ssl") +
                            alfrescoProperties.get("alfresco.baseUrl") + "/api/solr");
        } else {
            alfrescoVerifications.put("alfresco.solr.api.url",
                    "http://" + alfrescoProperties.get("alfresco.host") + ":" +
                            alfrescoProperties.get("alfresco.port") +
                            alfrescoProperties.get("alfresco.baseUrl") + "/api/solr");
        }
        SolrKeyResourceLoader keyResourceLoader = new SolrKeyResourceLoader(loader);
        SOLRAPIClientFactory clientFactory = new SOLRAPIClientFactory();
        SOLRAPIClient repositoryClient = clientFactory.getSOLRAPIClient(
                coreProperties, keyResourceLoader,
                AlfrescoSolrDataModel.getInstance().getDictionaryService(CMISStrictDictionaryService.DEFAULT),
                AlfrescoSolrDataModel.getInstance().getNamespaceDAO());
        String alfrescoConnection = OK;
        try {
            repositoryClient.getAclReaders(List.of(new Acl(1,1)));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            alfrescoConnection = ERROR + e.getMessage();
        }
        alfrescoVerifications.put("connection", alfrescoConnection);

        if (HttpClientFactory.SecureCommsType.getType(secureCommsType) == HttpClientFactory.SecureCommsType.HTTPS) {

            Properties jvmProperties = System.getProperties();

            String keystoreType = coreProperties.getProperty("alfresco.encryption.ssl.keystore.type", "JCEKS");
            String keystoreLocation =
                    coreProperties.getProperty("alfresco.encryption.ssl.keystore.location", "ssl.repo.client.keystore");
            String keystorePassword = jvmProperties.getProperty("ssl-keystore.password");

            String truststoreType = coreProperties.getProperty("alfresco.encryption.ssl.truststore.type", "JCEKS");
            String truststoreLocation =
                    coreProperties.getProperty("alfresco.encryption.ssl.truststore.location", "ssl.repo.client.truststore");
            String truststorePassword = jvmProperties.getProperty("ssl-truststore.password");

            alfrescoVerifications.put("endpoint",
                    convertToMap(CryptoUtils.getTlsEndpointParameters(
                            alfrescoProperties.get("alfresco.host"),
                            Integer.parseInt(alfrescoProperties.get("alfresco.port.ssl")),
                            keystoreType, keystoreLocation, keystorePassword.toCharArray(),
                            truststoreType, truststoreLocation, truststorePassword.toCharArray())));

        }
        alfresco.put("verifications", alfrescoVerifications);

        rsp.add("alfresco", alfresco);

    }

    /**
     * Adds Solr details to the SolrQueryResponse.
     *
     * @param rsp            The SolrQueryResponse object to which Solr details will be added.
     * @param coreProperties Properties of the Solr core.
     */
    public static void addSolrDetails(SolrQueryResponse rsp, Properties coreProperties) {

        Map<String, Object> solr = new LinkedHashMap<>();

        Properties jvmProperties = System.getProperties();
        String secureCommsType = coreProperties.getProperty("alfresco.secureComms", "none");
        solr.put("protocol", secureCommsType);
        if (HttpClientFactory.SecureCommsType.getType(secureCommsType) == HttpClientFactory.SecureCommsType.HTTPS) {

            Map<String, Object> keystore = new LinkedHashMap<>();

            String keystoreType = coreProperties.getProperty("alfresco.encryption.ssl.keystore.type", "JCEKS");
            String keystoreLocation =
                    coreProperties.getProperty("alfresco.encryption.ssl.keystore.location", "ssl.repo.client.keystore");
            String keystorePassword = jvmProperties.getProperty("ssl-keystore.password");
            String[] keystoreAliases = new String[]{};
            if (jvmProperties.getProperty("ssl-keystore.aliases") != null) {
                keystoreAliases = jvmProperties.getProperty("ssl-keystore.aliases").split(",");
            }

            Map<String, Object> keystoreProperties = new LinkedHashMap<>();
            keystoreProperties.put("alfresco.encryption.ssl.keystore.type", keystoreType);
            keystoreProperties.put("alfresco.encryption.ssl.keystore.location", keystoreLocation);
            keystore.put("properties", keystoreProperties);

            Map<String, Object> keystoreEnv = new LinkedHashMap<>();
            keystoreEnv.put("ssl-keystore.password", keystorePassword);
            keystoreEnv.put("ssl-keystore.aliases", String.join(",", keystoreAliases));
            keystore.put("environment", keystoreEnv);

            keystore.put("verifications",
                    convertToMap(CryptoUtils.verifyKeyStore(keystoreType, keystoreLocation, keystorePassword.toCharArray(), keystoreAliases)));
            solr.put("keystore", keystore);

            Map<String, Object> truststore = new LinkedHashMap<>();

            String truststoreType = coreProperties.getProperty("alfresco.encryption.ssl.truststore.type", "JCEKS");
            String truststoreLocation =
                    coreProperties.getProperty("alfresco.encryption.ssl.truststore.location", "ssl.repo.client.truststore");
            String truststorePassword = jvmProperties.getProperty("ssl-truststore.password");
            String[] truststoreAliases = new String[]{};
            if (jvmProperties.getProperty("ssl-truststore.aliases") != null) {
                truststoreAliases = jvmProperties.getProperty("ssl-truststore.aliases").split(",");
            }

            Map<String, Object> truststoreProperties = new LinkedHashMap<>();
            truststoreProperties.put("alfresco.encryption.ssl.truststore.type", truststoreType);
            truststoreProperties.put("alfresco.encryption.ssl.truststore.location", truststoreLocation);
            truststore.put("properties", truststoreProperties);

            Map<String, Object> truststoreEnv = new LinkedHashMap<>();
            truststoreEnv.put("ssl-truststore.password", truststorePassword);
            truststoreEnv.put("ssl-truststore.aliases", String.join(",", truststoreAliases));
            truststore.put("environment", truststoreEnv);

            truststore.put("verifications",
                    convertToMap(CryptoUtils.verifyKeyStore(truststoreType, truststoreLocation, truststorePassword.toCharArray(), truststoreAliases)));
            solr.put("truststore", truststore);

        } else {

            solr.put("secret", coreProperties.getProperty("alfresco.secureComms.secret", ""));
            solr.put("header", coreProperties.getProperty("alfresco.secureComms.secret.header", ""));

        }

        rsp.add("solr", solr);
    }

    private static Map<String, Object> convertToMap(Object object) {
        ObjectMapper oMapper = new ObjectMapper();
        return oMapper.convertValue(object, Map.class);
    }

}
