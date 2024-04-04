package org.alfresco.repository.admin;

import org.alfresco.crypto.CryptoUtils;
import org.alfresco.crypto.Endpoint;
import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class SearchClientWebScript extends DeclarativeWebScript {

    private Properties globalProperties;
    private SearchService searchService;

    public void setGlobalProperties(Properties globalProperties) {
        this.globalProperties = globalProperties;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        Properties jvmProperties = System.getProperties();

        Map<String, Object> model = new HashMap<>();
        String secureCommsType = globalProperties.getProperty("solr.secureComms");

        Map<String, String> solrProperties = new LinkedHashMap<>();
        solrProperties.put("solr.host", globalProperties.getProperty("solr.host"));
        solrProperties.put("solr.port", globalProperties.getProperty("solr.port"));
        solrProperties.put("solr.port.ssl", globalProperties.getProperty("solr.port.ssl"));
        solrProperties.put("solr.baseUrl", globalProperties.getProperty("solr.baseUrl", "/solr"));
        solrProperties.put("solr.secureComms", secureCommsType);
        if (HttpClientFactory.SecureCommsType.getType(secureCommsType) == HttpClientFactory.SecureCommsType.HTTPS) {
            solrProperties.put("solr.url",
                    "https://" + solrProperties.get("solr.host") + ":" +
                            solrProperties.get("solr.port.ssl") +
                            solrProperties.get("solr.baseUrl"));
        } else {
            solrProperties.put("solr.url",
                    "http://" + solrProperties.get("solr.host") + ":" +
                            solrProperties.get("solr.port") +
                            solrProperties.get("solr.baseUrl"));
        }
        model.put("solr", solrProperties);

        if (HttpClientFactory.SecureCommsType.getType(secureCommsType) == HttpClientFactory.SecureCommsType.HTTPS) {

            String ksType = globalProperties.getProperty("encryption.ssl.keystore.type");
            String ksLocation = globalProperties.getProperty("encryption.ssl.keystore.location");
            String ksPassword = jvmProperties.getProperty("ssl-keystore.password");
            String tsType = globalProperties.getProperty("encryption.ssl.truststore.type");
            String tsLocation = globalProperties.getProperty("encryption.ssl.truststore.location");
            String tsPassword = jvmProperties.getProperty("ssl-truststore.password");

            Endpoint endpoint = CryptoUtils.getTlsEndpointParameters(
                    solrProperties.get("solr.host"),
                    Integer.parseInt(solrProperties.get("solr.port.ssl")),
                    ksType, ksLocation, ksPassword.toCharArray(),
                    tsType, tsLocation, tsPassword.toCharArray());

            model.put("endpoint", convertToMap(endpoint));

        }

        String connection = "true";
        try {
            searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
                    SearchService.LANGUAGE_FTS_ALFRESCO,
                    "test");
        } catch (RuntimeException re) {
            connection = "false";
            model.put("connectionError", findExceptionCause(re).getMessage());
            re.printStackTrace(System.err);
        }
        model.put("connection", connection);

        if (HttpClientFactory.SecureCommsType.getType(secureCommsType) == HttpClientFactory.SecureCommsType.HTTPS) {

            String ksType = globalProperties.getProperty("encryption.ssl.keystore.type");
            String ksLocation = globalProperties.getProperty("encryption.ssl.keystore.location");
            String ksPassword = jvmProperties.getProperty("ssl-keystore.password");
            String[] keystoreAliases = new String[]{};
            if (jvmProperties.getProperty("ssl-keystore.aliases") != null) {
                keystoreAliases = jvmProperties.getProperty("ssl-keystore.aliases").split(",");
            }

            Map<String, Object> keystore = convertToMap(CryptoUtils.verifyKeyStore(ksType, ksLocation, ksPassword.toCharArray(), keystoreAliases));
            keystore.put("type", ksType);
            keystore.put("location", ksLocation);
            keystore.put("password", ksPassword);
            keystore.put("aliases", Arrays.toString(keystoreAliases));

            model.put("keystore", keystore);

            String tsType = globalProperties.getProperty("encryption.ssl.truststore.type");
            String tsLocation = globalProperties.getProperty("encryption.ssl.truststore.location");
            String tsPassword = jvmProperties.getProperty("ssl-truststore.password");
            String[] truststoreAliases = new String[]{};
            if (jvmProperties.getProperty("ssl-truststore.aliases") != null) {
                truststoreAliases = jvmProperties.getProperty("ssl-truststore.aliases").split(",");
            }

            Map<String, Object> truststore = convertToMap(CryptoUtils.verifyKeyStore(tsType, tsLocation, tsPassword.toCharArray(), truststoreAliases));
            truststore.put("type", tsType);
            truststore.put("location", tsLocation);
            truststore.put("password", tsPassword);
            truststore.put("aliases", Arrays.toString(truststoreAliases));

            model.put("truststore", truststore);

        } else {

            model.put("secret", jvmProperties.getProperty("solr.sharedSecret", ""));
            model.put("header", jvmProperties.getProperty("solr.sharedSecret.header", "X-Alfresco-Search-Secret"));

        }

        return model;

    }

    private static Map<String, Object> convertToMap(Object object) {
        ObjectMapper oMapper = new ObjectMapper();
        return oMapper.convertValue(object, Map.class);
    }

    private static Throwable findExceptionCause(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

}