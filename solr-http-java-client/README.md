# SOLR HTTP Java Client - REST API Action

Available in https://localhost:8983/solr/admin/cores?action=HTTP-CLIENT&coreName=alfresco

Sample response:

```xml
<response>
   <lst name="responseHeader">
      <int name="status">0</int>
      <int name="QTime">145</int>
   </lst>
   <lst name="alfresco">
      <lst name="properties">
         <str name="alfresco.host">alfresco</str>
         <str name="alfresco.port">8443</str>
         <str name="alfresco.port.ssl">8443</str>
         <str name="alfresco.baseUrl">/alfresco</str>
         <str name="alfresco.secureComms">https</str>
      </lst>
      <lst name="verifications">
         <str name="alfresco.solr.api.url">https://alfresco:8443/alfresco/api/solr</str>
         <str name="connection">OK</str>
         <lst name="endpoint">
            <arr name="supportedProcotols">
               <str>TLSv1.3</str>
               <str>TLSv1.2</str>
            </arr>
            <arr name="trustedCertificates">
               <lst>
                  <str name="name">CN=Custom Alfresco Repository,OU=Unknown,O=Alfresco Software Ltd.,ST=UK,C=GB</str>
                  <str name="expiration">2031-12-14 08:50 AM UTC</str>
               </lst>
               <lst>
                  <str name="name">CN=Custom Alfresco CA,OU=Unknown,O=Alfresco Software Ltd.,L=Maidenhead,ST=UK,C=GB</str>
                  <str name="expiration">2041-12-11 08:50 AM UTC</str>
               </lst>
            </arr>
         </lst>
      </lst>
   </lst>
   <lst name="solr">
      <str name="protocol">https</str>
      <lst name="keystore">
         <lst name="properties">
            <str name="alfresco.encryption.ssl.keystore.type">PKCS12</str>
            <str name="alfresco.encryption.ssl.keystore.location">/opt/alfresco-search-services/keystore/ssl-repo-client.keystore</str>
         </lst>
         <lst name="environment">
            <str name="ssl-keystore.password">keystore</str>
            <str name="ssl-keystore.aliases">ssl-alfresco-ca,ssl-repo-client</str>
         </lst>
         <lst name="verifications">
            <str name="status">OK</str>
            <arr name="aliasDetailsList">
               <lst>
                  <str name="alias">ssl-repo-client</str>
                  <str name="type">KEY</str>
                  <str name="subject">CN=Custom Alfresco Repository Client,OU=Unknown,O=Alfresco Software Ltd.,ST=UK,C=GB</str>
                  <str name="issuer">CN=Custom Alfresco CA,OU=Unknown,O=Alfresco Software Ltd.,L=Maidenhead,ST=UK,C=GB</str>
                  <str name="expiration">2031-12-14 08:50 AM UTC</str>
                  <str name="algorithm">1.2.840.113549.1.1.11 - SHA256withRSA</str>
                  <str name="size">2048 bits</str>
                  <arr name="usages">
                     <lst>
                        <str name="oid">1.3.6.1.5.5.7.3.1</str>
                        <str name="name">Server Authentication</str>
                     </lst>
                  </arr>
               </lst>
               <lst>
                  <str name="alias">ssl-alfresco-ca</str>
                  <str name="type">TRUST</str>
                  <str name="subject">CN=Custom Alfresco CA,OU=Unknown,O=Alfresco Software Ltd.,L=Maidenhead,ST=UK,C=GB</str>
                  <str name="issuer">CN=Custom Alfresco CA,OU=Unknown,O=Alfresco Software Ltd.,L=Maidenhead,ST=UK,C=GB</str>
                  <str name="expiration">2041-12-11 08:50 AM UTC</str>
                  <str name="algorithm">1.2.840.113549.1.1.11 - SHA256withRSA</str>
                  <str name="size">2048 bits</str>
                  <null name="usages" />
               </lst>
            </arr>
            <arr name="aliasExistenceList">
               <lst>
                  <str name="alias">ssl-alfresco-ca</str>
                  <bool name="exists">true</bool>
               </lst>
               <lst>
                  <str name="alias">ssl-repo-client</str>
                  <bool name="exists">true</bool>
               </lst>
            </arr>
         </lst>
      </lst>
      <lst name="truststore">
         <lst name="properties">
            <str name="alfresco.encryption.ssl.truststore.type">PKCS12</str>
            <str name="alfresco.encryption.ssl.truststore.location">/opt/alfresco-search-services/keystore/ssl-repo-client.truststore</str>
         </lst>
         <lst name="environment">
            <str name="ssl-truststore.password">truststore</str>
            <str name="ssl-truststore.aliases">ssl-alfresco-ca,ssl-repo,ssl-repo-client</str>
         </lst>
         <lst name="verifications">
            <str name="status">OK</str>
            <arr name="aliasDetailsList">
               <lst>
                  <str name="alias">ssl-alfresco-ca</str>
                  <str name="type">TRUST</str>
                  <str name="subject">CN=Custom Alfresco CA,OU=Unknown,O=Alfresco Software Ltd.,L=Maidenhead,ST=UK,C=GB</str>
                  <str name="issuer">CN=Custom Alfresco CA,OU=Unknown,O=Alfresco Software Ltd.,L=Maidenhead,ST=UK,C=GB</str>
                  <str name="expiration">2041-12-11 08:50 AM UTC</str>
                  <str name="algorithm">1.2.840.113549.1.1.11 - SHA256withRSA</str>
                  <str name="size">2048 bits</str>
                  <null name="usages" />
               </lst>
               <lst>
                  <str name="alias">ssl-repo</str>
                  <str name="type">TRUST</str>
                  <str name="subject">CN=Custom Alfresco Repository,OU=Unknown,O=Alfresco Software Ltd.,ST=UK,C=GB</str>
                  <str name="issuer">CN=Custom Alfresco CA,OU=Unknown,O=Alfresco Software Ltd.,L=Maidenhead,ST=UK,C=GB</str>
                  <str name="expiration">2031-12-14 08:50 AM UTC</str>
                  <str name="algorithm">1.2.840.113549.1.1.11 - SHA256withRSA</str>
                  <str name="size">2048 bits</str>
                  <arr name="usages">
                     <lst>
                        <str name="oid">1.3.6.1.5.5.7.3.1</str>
                        <str name="name">Server Authentication</str>
                     </lst>
                  </arr>
               </lst>
               <lst>
                  <str name="alias">ssl-repo-client</str>
                  <str name="type">TRUST</str>
                  <str name="subject">CN=Custom Alfresco Repository Client,OU=Unknown,O=Alfresco Software Ltd.,ST=UK,C=GB</str>
                  <str name="issuer">CN=Custom Alfresco CA,OU=Unknown,O=Alfresco Software Ltd.,L=Maidenhead,ST=UK,C=GB</str>
                  <str name="expiration">2031-12-14 08:50 AM UTC</str>
                  <str name="algorithm">1.2.840.113549.1.1.11 - SHA256withRSA</str>
                  <str name="size">2048 bits</str>
                  <arr name="usages">
                     <lst>
                        <str name="oid">1.3.6.1.5.5.7.3.1</str>
                        <str name="name">Server Authentication</str>
                     </lst>
                  </arr>
               </lst>
            </arr>
            <arr name="aliasExistenceList">
               <lst>
                  <str name="alias">ssl-alfresco-ca</str>
                  <bool name="exists">true</bool>
               </lst>
               <lst>
                  <str name="alias">ssl-repo</str>
                  <bool name="exists">true</bool>
               </lst>
               <lst>
                  <str name="alias">ssl-repo-client</str>
                  <bool name="exists">true</bool>
               </lst>
            </arr>
         </lst>
      </lst>
   </lst>
</response>
```