ARG ALFRESCO_TAG
FROM alfresco/alfresco-content-repository-community:${ALFRESCO_TAG}

ARG TOMCAT_DIR=/usr/local/tomcat

ARG TRUSTSTORE_LOCATION
ARG TRUSTSTORE_TYPE
ARG TRUSTSTORE_PASS
ARG KEYSTORE_LOCATION
ARG KEYSTORE_TYPE
ARG KEYSTORE_PASS
ARG CERT_ALIAS
ARG CERT_TYPE

ENV TRUSTSTORE_LOCATION=$TRUSTSTORE_LOCATION \
    TRUSTSTORE_TYPE=$TRUSTSTORE_TYPE \
    TRUSTSTORE_PASS=$TRUSTSTORE_PASS \
    KEYSTORE_LOCATION=$KEYSTORE_LOCATION \
    KEYSTORE_TYPE=$KEYSTORE_TYPE \
    KEYSTORE_PASS=$KEYSTORE_PASS \
    CERT_ALIAS=$CERT_ALIAS \
    CERT_TYPE=$CERT_TYPE

USER root

# Add mTLS connector to Tomcat configuration using port 8443.
# This connector will listen to Solr REST API requests, while existing connector (8080) will listen to other REST API requests.
RUN ESCAPED_TRUSTSTORE_LOCATION=${TRUSTSTORE_LOCATION//\//\\/} && \
ESCAPED_KEYSTORE_LOCATION=${KEYSTORE_LOCATION//\//\\/} && \
sed -i "s/\
[[:space:]]\+<\/Engine>/\n\
        <\/Engine>\n\
        <Connector port=\"8443\" protocol=\"org.apache.coyote.http11.Http11NioProtocol\"\n\
            connectionTimeout=\"20000\" maxThreads=\"150\"\n\
            SSLEnabled=\"true\" scheme=\"https\" secure=\"true\"\n\
            defaultSSLHostConfigName=\"localhost\">\n\
                <SSLHostConfig hostName=\"localhost\" protocols=\"TLSv1.3\"\n\
                    certificateVerification=\"required\"\n\
                    truststoreFile=\"${ESCAPED_TRUSTSTORE_LOCATION}\"\n\
                    truststorePassword=\"${TRUSTSTORE_PASS}\" truststoreType=\"${TRUSTSTORE_TYPE}\">\n\
                    <Certificate certificateKeystoreFile=\"${ESCAPED_KEYSTORE_LOCATION}\"\n\
                       certificateKeyAlias=\"${CERT_ALIAS}\" type=\"${CERT_TYPE}\"\n\
                       certificateKeystorePassword=\"${KEYSTORE_PASS}\" certificateKeystoreType=\"${KEYSTORE_TYPE}\"\/>\n\
                <\/SSLHostConfig>\n\
        <\/Connector>/g" ${TOMCAT_DIR}/conf/server.xml

USER alfresco        