# mTLS Configuration App

The Alfresco Repository may fail to boot depending on configuration parameter issues. To troubleshoot such scenarios, use this application.

The project can be built with Maven:

```sh
mvn clean package
```

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