# alfresco-mtls-debugging-kit

Set of tools to debug mTLS configuration issues when installing Alfresco Services using mTLS protocol:

* [addons](addons) folder includes extensions that provide detailed information related to mTLS configuration
  * [alfresco-http-java-client](addons/alfresco-http-java-client) is an Alfresco Repository addon for the Admin Console that adds the page "Search Client"
  * [solr-http-java-client](addons/solr-http-java-client) is a SOLR plugin for the Admin REST API that adds the action "HTTP-CLIENT"
* [apps](apps) folder includes applications to help identifying issues in mTLS configuration
  * [mtls-conf-app](apps/mtls-conf-app) is a command line application to verify mTLS endpoint (server) and keystores (client)
* [common](common) folder includes the library `crypto-utils`, that is used as third party dependency in addons and apps
* [docker](docker) folder includes a sample mTLS configuration for Alfresco using keystores provided by [alfresco-ssl-generator](https://github.com/alfresco/alfresco-ssl-generator). This Docker Compose deployment also applies the `addons` to Alfresco Repository and SOLR
* [step-ca](step-ca) folder includes a lab environment to generate ECC certificates for ECDSA and package required keystores for Alfresco mTLS configuration