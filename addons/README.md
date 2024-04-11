# Web Admin Tools

This folder includes Web Admin Tools for Repository and SOLR. You can open them in a web browser and see information about mTLS setup, like where it connects to, the keys and certificates it uses, and the passwords.

The [docker](../docker) folder applies both tools using Docker Compose. Deploying them locally requires following steps.

## alfresco-http-java-client

**Admin Console Page for Alfresco Repository**

* Installation: copy [alfresco-http-java-client-0.8.0.jar](https://github.com/aborroy/alfresco-mtls-debugging-kit/releases/download/0.8.0/alfresco-http-java-client-0.8.0.jar) and [crypto-utils-0.8.0.jar](https://github.com/aborroy/alfresco-mtls-debugging-kit/releases/download/0.8.0/crypto-utils-0.8.0.jar) files to `${TOMCAT_DIR}/webapps/alfresco/WEB-INF/lib/` and re-start the Alfresco service.
* Url: http://localhost:8080/alfresco/s/admin/admin-search-client
* Credentials: admin/admin

## solr-http-java-client

**REST API Action for Apache Solr**

* Installation
  * Copy [solr-http-java-client-0.8.0.jar](https://github.com/aborroy/alfresco-mtls-debugging-kit/releases/download/0.8.0/solr-http-java-client-0.8.0.jar) and [crypto-utils-0.8.0.jar](https://github.com/aborroy/alfresco-mtls-debugging-kit/releases/download/0.8.0/crypto-utils-0.8.0.jar) files to `${SOLR_DIR}/solr/server/solr-webapp/webapp/WEB-INF/lib` 
  * Overwrite `${SOLR_DIR}/solrhome/solr.xml` file with content from [solr.xml](https://github.com/aborroy/alfresco-mtls-debugging-kit/blob/main/docker/search/config/solr.xml)
  * Re-start Solr
* Url: https://localhost:8983/solr/admin/cores?action=HTTP-CLIENT&coreName=alfresco
* Credentials: [browser.p12](docker/keystores/client) client certificate
