# SOLR HTTP Java Client - REST API Action

Available in https://localhost:8983/solr/admin/cores?action=HTTP-CLIENT&coreName=alfresco

Sample response:

```xml
<response>
   <lst name="responseHeader">
      <int name="status">0</int>
      <int name="QTime">173</int>
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
            <str name="tlsProcotol">TLSv1.3</str>
            <arr name="trustedCertificates">
               <lst>
                  <str name="name">CN=Search,OU=Alfresco,O=Hyland,ST=OH,C=US</str>
                  <str name="expiration">2034-04-06 14:56 PM UTC</str>
               </lst>
               <lst>
                  <str name="name">CN=Alfresco CA,OU=Alfresco,O=Hyland,L=Cleveland,ST=OH,C=US</str>
                  <str name="expiration">2044-04-03 14:56 PM UTC</str>
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
            <str name="alfresco.encryption.ssl.keystore.location">/opt/alfresco-search-services/keystore/ssl.repo.client.keystore</str>
         </lst>
         <lst name="environment">
            <str name="ssl-keystore.password">keystore</str>
            <str name="ssl-keystore.aliases"/>
         </lst>
         <lst name="verifications">
            <str name="status">OK</str>
            <arr name="aliasDetailsList">
               <lst>
                  <str name="alias">ssl.repo.client</str>
                  <str name="type">KEY</str>
                  <str name="subject">CN=Search,OU=Alfresco,O=Hyland,ST=OH,C=US</str>
                  <str name="issuer">CN=Alfresco CA,OU=Alfresco,O=Hyland,L=Cleveland,ST=OH,C=US</str>
                  <str name="expiration">2034-04-06 14:56 PM UTC</str>
                  <str name="algorithm">1.2.840.113549.1.1.11 - SHA256withRSA</str>
                  <str name="size">2048 bits</str>
                  <arr name="usages">
                     <lst>
                        <str name="oid">1.3.6.1.5.5.7.3.1</str>
                        <str name="name">Server Authentication</str>
                     </lst>
                     <lst>
                        <str name="oid">1.3.6.1.5.5.7.3.2</str>
                        <str name="name">Client Authentication</str>
                     </lst>
                  </arr>
               </lst>
            </arr>
            <arr name="aliasExistenceList"/>
         </lst>
      </lst>
      <lst name="truststore">
         <lst name="properties">
            <str name="alfresco.encryption.ssl.truststore.type">PKCS12</str>
            <str name="alfresco.encryption.ssl.truststore.location">/opt/alfresco-search-services/keystore/ssl.repo.client.truststore</str>
         </lst>
         <lst name="environment">
            <str name="ssl-truststore.password">truststore</str>
            <str name="ssl-truststore.aliases"/>
         </lst>
         <lst name="verifications">
            <str name="status">OK</str>
            <arr name="aliasDetailsList">
               <lst>
                  <str name="alias">alfresco.ca</str>
                  <str name="type">TRUST</str>
                  <str name="subject">CN=Alfresco CA,OU=Alfresco,O=Hyland,L=Cleveland,ST=OH,C=US</str>
                  <str name="issuer">CN=Alfresco CA,OU=Alfresco,O=Hyland,L=Cleveland,ST=OH,C=US</str>
                  <str name="expiration">2044-04-03 14:56 PM UTC</str>
                  <str name="algorithm">1.2.840.113549.1.1.11 - SHA256withRSA</str>
                  <str name="size">2048 bits</str>
                  <null name="usages"/>
               </lst>
               <lst>
                  <str name="alias">ssl.repo</str>
                  <str name="type">TRUST</str>
                  <str name="subject">CN=Repository,OU=Alfresco,O=Hyland,ST=OH,C=US</str>
                  <str name="issuer">CN=Alfresco CA,OU=Alfresco,O=Hyland,L=Cleveland,ST=OH,C=US</str>
                  <str name="expiration">2034-04-06 14:56 PM UTC</str>
                  <str name="algorithm">1.2.840.113549.1.1.11 - SHA256withRSA</str>
                  <str name="size">2048 bits</str>
                  <arr name="usages">
                     <lst>
                        <str name="oid">1.3.6.1.5.5.7.3.1</str>
                        <str name="name">Server Authentication</str>
                     </lst>
                     <lst>
                        <str name="oid">1.3.6.1.5.5.7.3.2</str>
                        <str name="name">Client Authentication</str>
                     </lst>
                  </arr>
               </lst>
            </arr>
            <arr name="aliasExistenceList"/>
         </lst>
      </lst>
   </lst>
</response>
```