ARG SEARCH_TAG
FROM alfresco/alfresco-search-services:${SEARCH_TAG}

ARG ALFRESCO_HOSTNAME
ARG SOLR_HOSTNAME
ENV ALFRESCO_HOSTNAME $ALFRESCO_HOSTNAME
ENV SOLR_HOSTNAME $SOLR_HOSTNAME

RUN sed -i '/^bash.*/i sed -i "'"s/alfresco.host=localhost/alfresco.host=${ALFRESCO_HOSTNAME}/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties\n' \
    ${DIST_DIR}/solr/bin/search_config_setup.sh && \
    sed -i '/^bash.*/i sed -i "'"s/solr.host=localhost/solr.host=${SOLR_HOSTNAME}/g"'" ${DIST_DIR}/solrhome/conf/shared.properties\n' \
    ${DIST_DIR}/solr/bin/search_config_setup.sh

RUN sed -i '/^bash.*/i sed -i "'"s/alfresco.secureComms=none/alfresco.secureComms=https/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties\n' \
    ${DIST_DIR}/solr/bin/search_config_setup.sh

ARG TRUSTSTORE_TYPE
ENV TRUSTSTORE_TYPE $TRUSTSTORE_TYPE
ARG KEYSTORE_TYPE
ENV KEYSTORE_TYPE $KEYSTORE_TYPE

# TRUSTSTORE_LOCATION: /opt/alfresco-search-services/keystore/ssl-repo-client.truststore
# KEYSTORE_LOCATION: /opt/alfresco-search-services/keystore/ssl-repo-client.keystore

RUN sed -i '/^bash.*/i \
  sed -i "'"s/alfresco.encryption.ssl.keystore.location=.*/alfresco.encryption.ssl.keystore.location=\\\/opt\\\/alfresco-search-services\\\/keystore\\\/ssl-repo-client.keystore/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties && \
  sed -i "'"s/alfresco.encryption.ssl.keystore.type=.*/alfresco.encryption.ssl.keystore.type=${KEYSTORE_TYPE}/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties && \
  sed -i "'"s/alfresco.encryption.ssl.truststore.location=.*/alfresco.encryption.ssl.truststore.location=\\\/opt\\\/alfresco-search-services\\\/keystore\\\/ssl-repo-client.truststore/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties && \
  sed -i "'"s/alfresco.encryption.ssl.truststore.type=.*/alfresco.encryption.ssl.truststore.type=${TRUSTSTORE_TYPE}/g"'" ${DIST_DIR}/solrhome/templates/rerank/conf/solrcore.properties' \
  ${DIST_DIR}/solr/bin/search_config_setup.sh