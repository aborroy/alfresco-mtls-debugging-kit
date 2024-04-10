# How to renew Alfresco Keystores

This folder includes instructions to create a new set of keystores for Alfresco mTLS configuration. Instead of using [alfresco-ssl-generator](https://github.com/Alfresco/alfresco-ssl-generator/blob/master/ssl-tool/samples/community.sh) tool, [step-ca](https://smallstep.com/certificates/) service is providing EC certificates to be used with [ECDSA algoritm](https://en.wikipedia.org/wiki/Elliptic_Curve_Digital_Signature_Algorithm). The certificates are packaged as expected by the Alfresco platform.

## Run the step service

The file [compose.yaml](./compose.yaml) describes a [step-ca](https://smallstep.com/docs/step-ca/getting-started/) configuration to create a CA named `Alfresco`, that exposes the CA service using port `9000`. Additionally, CA files are stored in a local folder named `step`.

Start the container using the regular command:

```sh
docker compose up 
```

Install the [step CLI](https://smallstep.com/docs/step-cli/) installed on your local machine. When using Mac OS, `brew` package manager can be used:

```sh
brew install step
```

As having access to `root CA` key is required to issue certificates, retrieve the password using following command:

```sh
cat step/secrets/password
ZuSJLBo6uRtlvzGe0z1i5ReqU2tpncl19RBUIf5V
```

>> Note that `ZuSJLBo6uRtlvzGe0z1i5ReqU2tpncl19RBUIf5V` is the password generated in my local test, but this value should be different in your environment.

## Create EC certificates for ECDSA

Issue a new certificate for Repository service named `alfresco` that lasts for one year

```sh
step certificate create alfresco alfresco.crt alfresco.key \
--profile leaf --not-after=8760h --bundle --ca step/certs/root_ca.crt \
--ca-key step/secrets/root_ca_key
```

>> Use the password retrieved previously to access to `root_ca_key` and protect the new certificate with password `keystore`

Issue a new certificate for Search service named `solr` that lasts for one year

```sh
step certificate create solr solr.crt solr.key \
--profile leaf --not-after=8760h --bundle --ca step/certs/root_ca.crt \
--ca-key step/secrets/root_ca_key
```

>> Use the password retrieved previously to access to `root_ca_key` and protect the new certificate with password `keystore`

## Package Keystores for Alfresco service


Package the private key issued for `alfresco` in a PKCS12 keystore named `alfresco.pkcs12`

```sh
openssl pkcs12 -export -in ./alfresco.crt -inkey alfresco.key \
-out alfresco.pkcs12 -name alfresco -noiter -nomaciter
```

Package the public keys (for `solr` and `ca`) in a PKCS12 keystore named `alfresco-truststore.pkcs12`

```sh
keytool -import -alias solr -file ./solr.crt -keystore alfresco-truststore.pkcs12 \
-storetype PKCS12 -storepass truststore

keytool -import -alias ca -file step/certs/root_ca.crt -keystore alfresco-truststore.pkcs12 \
-storetype PKCS12 -storepass truststore
```

## Package Keystores for Search service

Package the private key issued for `solr` in a PKCS12 keystore named `solr.p12`

```sh
openssl pkcs12 -export -in ./solr.crt -inkey solr.key \
-out solr.pkcs12 -name solr -noiter -nomaciter
```

Package the public keys (for `alfresco` and `ca`) in a PKCS12 keystore named `solr-truststore.pkcs12`

```sh
keytool -import -alias alfresco -file ./alfresco.crt -keystore solr-truststore.pkcs12 \
-storetype PKCS12 -storepass truststore

keytool -import -alias ca -file step/certs/root_ca.crt -keystore solr-truststore.pkcs12 \
-storetype PKCS12 -storepass truststore
```

## Package a client `alfresco` certificate for browser

SOLR Web Console is protected by mTLS, so a client certificate is required to access the URL https://localhost:8983/solr

Package the private key issued for `alfresco` in a PKCS12 keystore named `browser.p12` (or use the already produced `alfresco.pkcs12`)

```sh
openssl pkcs12 -export -out browser.p12 -inkey alfresco.key -in alfresco.crt
```

## Modify Alfresco Docker Compose configuration to use the new certificates

Once keystore files are generated, copy the [docker](https://github.com/aborroy/alfresco-mtls-debugging-kit/tree/main/docker) folder to apply the new configuration.

Remove the `keystores` folder and build a new one with following structure:

```
keystores
├── alfresco
│   ├── alfresco-truststore.pkcs12
│   └── alfresco.pkcs12
├── client
│   └── browser.p12
└── solr
    ├── solr-truststore.pkcs12
    └── solr.pkcs12
```

>> All these keystores have been generated in previous steps. As additional information, the certificate alias in `alfresco.pkcs12` is `alfresco` and the certificates type is `EC`.

Update `compose.yaml` file with the new values:

```yaml
    alfresco:
        build:
          args:
            KEYSTORE_LOCATION: /usr/local/tomcat/keystore/alfresco.pkcs12
            TRUSTSTORE_LOCATION: /usr/local/tomcat/keystore/alfresco-truststore.pkcs12
            CERT_ALIAS: alfresco
            CERT_TYPE: EC
            JAVA_OPTS : >-
                -Dencryption.ssl.keystore.location=/usr/local/tomcat/keystore/alfresco.pkcs12
                -Dencryption.ssl.truststore.location=/usr/local/tomcat/keystore/alfresco-truststore.pkcs12

    solr6:
        build:
          args:
            KEYSTORE_LOCATION: /opt/alfresco-search-services/keystore/solr.pkcs12
            TRUSTSTORE_LOCATION: /opt/alfresco-search-services/keystore/solr-truststore.pkcs12
        environment:
            SOLR_SSL_KEY_STORE: "/opt/alfresco-search-services/keystore/solr.pkcs12"
            SOLR_SSL_TRUST_STORE: "/opt/alfresco-search-services/keystore/solr-truststore.pkcs12"
```

## Start Alfresco Platform with the new configuration

Use the regular command to start Docker Compose:

```sh
docker compose up 
```

You may verify that the platform is working as expected by using the [addons](https://github.com/aborroy/alfresco-mtls-debugging-kit/tree/main/addons) available in this project.

In addition, if `nmap` command is available in your environment, following command can be used:

```
nmap --script ssl-enum-ciphers -p 8983 localhost

PORT     STATE SERVICE
8983/tcp open  unknown
| ssl-enum-ciphers:
|   TLSv1.3:
|     ciphers:
|       TLS_AKE_WITH_AES_256_GCM_SHA384 (secp256r1) - A
|       TLS_AKE_WITH_AES_128_GCM_SHA256 (secp256r1) - A
|       TLS_AKE_WITH_CHACHA20_POLY1305_SHA256 (secp256r1) - A
|     cipher preference: server
|_  least strength: A

$ nmap --script ssl-enum-ciphers -p 8443 localhost

PORT     STATE SERVICE
8443/tcp open  https-alt
| ssl-enum-ciphers:
|   TLSv1.3:
|     ciphers:
|       TLS_AKE_WITH_AES_128_CCM_SHA256 (ecdh_x25519) - A
|       TLS_AKE_WITH_AES_128_GCM_SHA256 (ecdh_x25519) - A
|       TLS_AKE_WITH_AES_256_GCM_SHA384 (ecdh_x25519) - A
|       TLS_AKE_WITH_CHACHA20_POLY1305_SHA256 (ecdh_x25519) - A
|     cipher preference: client
|_  least strength: A

Nmap done: 1 IP address (1 host up) scanned in 0.10 seconds
```

This output means that during the TLSv1.3 handshake process, the client and server agree to use AES with a 256-bit key for encryption in GCM mode, SHA-384 for hashing, and Curve25519 for key exchange. These algorithms align with the best cryptographic practices.