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

## Sample mTLS deployment

The [docker](docker) folder provides a ready-to-use configuration for secure communication between Repository and Search using mTLS. In addition, [alfresco-http-java-client](addons/alfresco-http-java-client) and [solr-http-java-client](addons/solr-http-java-client) addons are applied.

The stack can be started using regular Docker Compose command:

```bash
cd docker
docker compose up
```

Services:

* Repository: http://localhost:8080/alfresco
* Share UI: http://localhost:8080/share
* ACA UI: http://localhost:8080/content-app
* Solr UI: https://localhost:8983/solr

Addons:

* Repository Admin Console - Search Client: http://localhost:8080/alfresco/s/admin/admin-search-client
* Solr HTTP-CLIENT action: https://localhost:8983/solr/admin/cores?action=HTTP-CLIENT&coreName=alfresco

Credentials:

* `admin`/`admin` for Repository, Share and ACA
* [browser.p12](docker/keystores/client) client certificate for Solr UI


## Web Admin Tools

The [addons](addons) folder includes Web Admin Tools for Repository and SOLR. You can open them in a web browser and see information about mTLS setup, like where it connects to, the keys and certificates it uses, and the passwords.

The [docker](docker) folder applies both tools using Docker Compose. Deploying them locally requires following steps.

**Admin Console Page for Alfresco Repository**

* Installation: copy [alfresco-http-java-client-0.8.0.jar](https://github.com/aborroy/alfresco-mtls-debugging-kit/releases/download/0.8.0/alfresco-http-java-client-0.8.0.jar) and [crypto-utils-0.8.0.jar](https://github.com/aborroy/alfresco-mtls-debugging-kit/releases/download/0.8.0/crypto-utils-0.8.0.jar) files to `${TOMCAT_DIR}/webapps/alfresco/WEB-INF/lib/` and re-start the Alfresco service.
* Url: http://localhost:8080/alfresco/s/admin/admin-search-client
* Credentials: admin/admin

**REST API Action for Apache Solr**

* Installation
  * Copy [solr-http-java-client-0.8.0.jar](https://github.com/aborroy/alfresco-mtls-debugging-kit/releases/download/0.8.0/solr-http-java-client-0.8.0.jar) and [crypto-utils-0.8.0.jar](https://github.com/aborroy/alfresco-mtls-debugging-kit/releases/download/0.8.0/crypto-utils-0.8.0.jar) files to `${SOLR_DIR}/solr/server/solr-webapp/webapp/WEB-INF/lib` 
  * Overwrite `${SOLR_DIR}/solrhome/solr.xml` file with content from [solr.xml](https://github.com/aborroy/alfresco-mtls-debugging-kit/blob/main/docker/search/config/solr.xml)
  * Re-start Solr
* Url: https://localhost:8983/solr/admin/cores?action=HTTP-CLIENT&coreName=alfresco
* Credentials: [browser.p12](docker/keystores/client) client certificate


## Troubleshooting App

The Alfresco Repository may fail to boot depending on configuration parameter issues. To troubleshoot such scenarios, use the [mtls-conf-app](apps/mtls-conf-app) application.

Default values for application properties are available in [application.properties](https://github.com/aborroy/alfresco-mtls-debugging-kit/blob/main/apps/mtls-conf-app/src/main/resources/application.properties) file.

Find the values you want to change, then start the Spring Boot application using the command line. For example, in the sample below, we're replacing the default value of `endpoint.host` with `192.168.1.137` instead of `localhost`.

```sh
java -jar target/mtls-conf-app-0.0.1.jar --endpoint.host=192.168.1.137
```

If errors occur, the output will detail the cause and include the complete stack trace of the exception.

```
ERRORS for ENDPOINT:
Current server setting '192.168.1.137' seems to be wrong.
Verify if you have access to server '192.168.1.137' or change the value to a different host name.
ERRORS DETAIL:
java.net.ConnectException: Operation timed out
    at java.base/sun.nio.ch.Net.connect0(Native Method)
    at java.base/sun.nio.ch.Net.connect(Net.java:579)
    at java.base/sun.nio.ch.Net.connect(Net.java:568)
```


## Keystores Generation Lab

This folder includes instructions to create a new set of keystores for Alfresco mTLS configuration. Instead of using [alfresco-ssl-generator](https://github.com/Alfresco/alfresco-ssl-generator/blob/master/ssl-tool/samples/community.sh) tool, [step-ca](https://smallstep.com/certificates/) service is providing EC certificates to be used with [ECDSA algoritm](https://en.wikipedia.org/wiki/Elliptic_Curve_Digital_Signature_Algorithm). The certificates are packaged as expected by the Alfresco platform.