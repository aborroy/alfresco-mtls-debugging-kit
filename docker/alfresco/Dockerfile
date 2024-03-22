ARG ALFRESCO_TAG
FROM alfresco/alfresco-content-repository-community:${ALFRESCO_TAG}

ARG TOMCAT_DIR=/usr/local/tomcat

ARG TRUSTSTORE_TYPE
ARG TRUSTSTORE_PASS
ARG KEYSTORE_TYPE
ARG KEYSTORE_PASS
ARG CERT_ALIAS

ENV TRUSTSTORE_TYPE=$TRUSTSTORE_TYPE \
    TRUSTSTORE_PASS=$TRUSTSTORE_PASS \
    KEYSTORE_TYPE=$KEYSTORE_TYPE \
    KEYSTORE_PASS=$KEYSTORE_PASS \
    CERT_ALIAS=$CERT_ALIAS

USER root

RUN sed -i "s/\
[[:space:]]\+<\/Engine>/\n\
        <\/Engine>\n\
        <Connector port=\"8443\" protocol=\"org.apache.coyote.http11.Http11NioProtocol\"\n\
            connectionTimeout=\"20000\" maxThreads=\"150\"\n\
            SSLEnabled=\"true\" scheme=\"https\" secure=\"true\"\n\
            defaultSSLHostConfigName=\"localhost\">\n\
                <SSLHostConfig hostName=\"localhost\" protocols=\"TLSv1.3\"\n\
                    certificateVerification=\"required\"\n\
                    truststoreFile=\"\/usr\/local\/tomcat\/keystore\/ssl.truststore\"\n\
                    truststorePassword=\"${TRUSTSTORE_PASS}\" truststoreType=\"${TRUSTSTORE_TYPE}\">\n\
                    <Certificate certificateKeystoreFile=\"\/usr\/local\/tomcat\/keystore\/ssl.keystore\"\n\
                       certificateKeyAlias=\"${CERT_ALIAS}\" type=\"RSA\"\n\
                       certificateKeystorePassword=\"${KEYSTORE_PASS}\" certificateKeystoreType=\"${KEYSTORE_TYPE}\"\/>\n\
                <\/SSLHostConfig>\n\
        <\/Connector>/g" ${TOMCAT_DIR}/conf/server.xml

USER alfresco        