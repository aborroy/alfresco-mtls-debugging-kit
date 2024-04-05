ARG SEARCH_TAG
FROM alfresco/alfresco-search-services:${SEARCH_TAG}

# Configuration is applied to "solrcore.properties" file located in rerank template, as environment variable for solr6 service
# SOLR_CREATE_ALFRESCO_DEFAULTS: "alfresco,archive" will copy this file to "solrhome/alfresco/conf/solrcore.properties" and
# "solrhome/archive/conf/solrcore.properties" when running the container. 
# You may apply this configuration directly to "solcore.properties" file located on each core when using a different deployment approach.

ARG ALFRESCO_HOSTNAME
ARG SOLR_HOSTNAME
ENV ALFRESCO_HOSTNAME=$ALFRESCO_HOSTNAME \
    SOLR_HOSTNAME=$SOLR_HOSTNAME

# Ensure mTLS configuration is enabled
RUN sed -i '/^bash.*/i sed -i "'"s/alfresco.host=localhost/alfresco.host=${ALFRESCO_HOSTNAME}/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties\n' \
    ${DIST_DIR}/solr/bin/search_config_setup.sh && \
    sed -i '/^bash.*/i sed -i "'"s/solr.host=localhost/solr.host=${SOLR_HOSTNAME}/g"'" ${DIST_DIR}/solrhome/conf/shared.properties\n' \
    ${DIST_DIR}/solr/bin/search_config_setup.sh
RUN sed -i '/^bash.*/i sed -i "'"s/alfresco.secureComms=none/alfresco.secureComms=https/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties\n' \
    ${DIST_DIR}/solr/bin/search_config_setup.sh

ARG TRUSTSTORE_TYPE
ARG TRUSTSTORE_LOCATION
ARG KEYSTORE_TYPE
ARG KEYSTORE_LOCATION
ENV TRUSTSTORE_TYPE=$TRUSTSTORE_TYPE \
    TRUSTSTORE_LOCATION=$TRUSTSTORE_LOCATION \
    KEYSTORE_TYPE=$KEYSTORE_TYPE \
    KEYSTORE_LOCATION=$KEYSTORE_LOCATION

# Set keystore and truststore values for mTLS Java Client
# Keystore passwords ("ssl-keystore.password" and "ssl-truststore.password") are taken from JAVA_OPTS
RUN ESCAPED_TRUSTSTORE_LOCATION=${TRUSTSTORE_LOCATION//\//\\\\/} && \
ESCAPED_KEYSTORE_LOCATION=${KEYSTORE_LOCATION//\//\\\\/} && \
sed -i '/^bash.*/i \
  sed -i "'"s/alfresco.encryption.ssl.keystore.location=.*/alfresco.encryption.ssl.keystore.location=${ESCAPED_KEYSTORE_LOCATION}/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties && \
  sed -i "'"s/alfresco.encryption.ssl.keystore.type=.*/alfresco.encryption.ssl.keystore.type=${KEYSTORE_TYPE}/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties && \
  sed -i "'"s/alfresco.encryption.ssl.truststore.location=.*/alfresco.encryption.ssl.truststore.location=${ESCAPED_TRUSTSTORE_LOCATION}/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties && \
  sed -i "'"s/alfresco.encryption.ssl.truststore.type=.*/alfresco.encryption.ssl.truststore.type=${TRUSTSTORE_TYPE}/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties' \
  ${DIST_DIR}/solr/bin/search_config_setup.sh