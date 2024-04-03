<#include "../admin-template.ftl" />

<@page title="Search Client" controller="/admin/admin-search-client" readonly=true>

    <div class="column-full">
    <h3>SOLR Endpoint</h3>
    </div>

    <div class="column-full">
        <div class="column-left">
            <br/><h4>Properties</h4>
            <@field value=solr["solr.host"] label="Host" description="solr.host: name of the Search Services server"/>
            <@field value=solr["solr.port"] label="Port" description="solr.port: plain HTTP port of the Search Services server" />
            <@field value=solr["solr.port.ssl"] label="SSL Port" description="solr.port.ssl: SSL HTTP port of the Search Services server" />
            <@field value=solr["solr.baseUrl"] label="Base Url" description="solr.baseUrl: base Url of the Search Services server" />
            <@field value=solr["solr.secureComms"] label="Comm Mode" description="solr.secureComms: communication mode for the Search Services server (https or secret)" />
            <@field value=solr["solr.url"] label="URL" description="URL to connect to Search Services for searching" />
        </div>

        <#if endpoint??>
        <div class="column-right">
            <br/><h4>Verifications</h4>
            <@status label="Connection" description="Status of connection from Repo to Solr" value=connection />
            <#if connectionError??>
              <font color="#8b0000">
                  <@field value=connectionError label="Error" description="Technical error when connecting from Repo to Solr. Check Alfresco Repository log for details."/>
              </font>
            </#if>
            <@field value=endpoint["tlsProcotol"] label="TLS Protocol" description="TLS Protocol to connect to Solr"/>
            <@field value="" label="Trusted Certificates" description="List of trusted certificates to connect to Solr"/>
            <#if endpoint["trustedCertificates"]??>
            <ul style="margin-left: 1em;">
                <#list endpoint["trustedCertificates"] as certificate>
                    <li style="border-top: 1px solid #ccc"><@field value=certificate.name label="Subject"/></li>
                    <li><@field value=certificate.expiration label="Expiration"/></li>
                </#list>
            </ul>
            </#if>
        </div>
        </#if>
    </div>

    <div class="column-full">
    <h3>Repository Java HTTP Client</h3>
    </div>

    <div class="column-full">
        <div class="column-left">

            <#if solr["solr.secureComms"] == "secret">
                <@section label="Secret Communication" />
                <@field value=secret label="Secret" description="solr.sharedSecret: secret word to connect to Solr"/>
                <@field value=header label="Header" description="solr.sharedSecret.header: http header name for secret to connect to Solr"/>
            </#if>

            <#if keystore??>
                <br/><h4>Keystore</h4>
                <@field value=keystore.status label="Status" description="Keystore verification"/>
                <br/><div class="control field">Properties</div>
                <@field value=keystore.type label="Type" description="encryption.ssl.keystore.type: keystore type"/>
                <@field value=keystore.location label="Path" description="encryption.ssl.keystore.location: keystore location"/>
                <br/><div class="control field">Environment Variables</div>
                <@field value=keystore.password label="Password" description="ssl-keystore.password: keystore password"/>
                <@field value=keystore.aliases label="Aliases" description="ssl-keystore.aliases: keystore aliases"/>
                <br/><div class="control field">Aliases existing in Keystore</div>
                <ul style="margin-left: 1em;">
                  <#list keystore["aliasExistenceList"] as alias>
                      <li>${alias.alias}: ${alias.exists?c}</li>
                  </#list>
                </ul>
            </#if>

        </div>

        <div class="column-right">

            <#if keystore??>
                <br/><h4>Keystore content</h4>
                <#assign keystoreCount=keystore["aliasDetailsList"]?size/>
                <@field value="" label="Certificates stored" description="There are ${keystoreCount} certificates in the keystore. At least one must be a KEY."/>
                <ul style="margin-left: 1em;">
                <#list keystore["aliasDetailsList"] as alias>
                    <li style="border-top: 1px solid #ccc"><@field value=alias.alias label="Alias"/></li>
                    <li><@field value=alias.type label="Type"/></li>
                    <li><@field value=alias.subject label="Subject"/></li>
                    <li><@field value=alias.issuer label="Issuer"/></li>
                    <li><@field value=alias.expiration label="Expiration"/></li>
                    <li><@field value=alias.algorithm label="Algorithm"/></li>
                    <li><@field value=alias.size label="Size"/></li>
                    <#if alias["usages"]??>
                      <@field value="" label="Usages" />
                      <#list alias["usages"] as usage>
                        <li style="margin-left: 2em;">${usage.name} - ${usage.oid}</li>
                      </#list>
                    </#if>
                    <br/>
                </#list>
                </ul>
            </#if>

        </div>

    </div>

    <div class="column-full">

        <div class="column-left">
            <#if truststore??>
                <br/><h4>Truststore</h4>
                <@field value=truststore.status label="Status" description="Truststore verification"/>
                <br/><div class="control field">Properties</div>
                <@field value=truststore.type label="Type" description="encryption.ssl.truststore.type: truststore type"/>
                <@field value=truststore.location label="Path" description="encryption.ssl.truststore.location: truststore location"/>
                <br/><div class="control field">Environment Variables</div>
                <@field value=truststore.password label="Password" description="ssl-truststore.password: truststore password"/>
                <@field value=truststore.aliases label="Aliases" description="ssl-truststore.aliases: keystore aliases"/>
                <br/><div class="control field">Aliases existing in Truststore</div>
                <ul style="margin-left: 1em;">
                  <#list truststore["aliasExistenceList"] as alias>
                      <li>${alias.alias}: ${alias.exists?c}</li>
                  </#list>
                </ul>
            </#if>
        </div>

        <div class="column-right">
            <#if truststore??>
                <br/><h4>Truststore content</h4>
                <#assign truststoreCount=truststore["aliasDetailsList"]?size/>
                <@field value="" label="Certificates stored" description="There are ${truststoreCount} certificates in the truststore."/>
                <ul style="margin-left: 1em;">
                  <#list truststore["aliasDetailsList"] as alias>
                      <li style="border-top: 1px solid #ccc"><@field value=alias.alias label="Alias"/></li>
                      <li><@field value=alias.type label="Type"/></li>
                      <li><@field value=alias.subject label="Subject"/></li>
                      <li><@field value=alias.issuer label="Issuer"/></li>
                      <li><@field value=alias.expiration label="Expiration"/></li>
                      <li><@field value=alias.algorithm label="Algorithm"/></li>
                      <li><@field value=alias.size label="Size"/></li>
                      <#if alias["usages"]??>
                        <@field value="" label="Usages" />
                        <#list alias["usages"] as usage>
                          <li style="margin-left: 2em;">${usage.name} - ${usage.oid}</li>
                        </#list>
                      </#if>
                      <br/>
                  </#list>
                </ul>
            </#if>
        </div>

    </div>

    <br/>

</@page>