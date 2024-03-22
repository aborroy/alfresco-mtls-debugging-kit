package org.alfresco.solr;

import org.alfresco.solr.action.HttpClientAction;
import org.alfresco.solr.security.SecretSharedPropertyCollector;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.CoreDescriptorDecorator;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static java.util.Optional.ofNullable;

/**
 * Extension for AlfrescoCoreAdminHandler.
 * <p/>
 * Using this class requires modifying default "solr.xml" configuration to:
 * <p/>
 * <code>
 *    <solr>
 *        <str name="adminHandler">${adminHandler:org.alfresco.solr.EnhancedAlfrescoCoreAdminHandler}</str>
 *    </solr>
 * </code>
 * <p/>
 * Available as a new action HTTP-CLIENT for the Core Admin REST API.
 * <p/>
 * Sample invocation:
 *   <a href="https://localhost:8983/solr/admin/cores?action=HTTP-CLIENT&coreName=alfresco">https://localhost:8983/solr/admin/cores?action=HTTP-CLIENT&coreName=alfresco</a>
 * <p/>
 * The action returns Solr HTTP Client details and verifications.
 */
public class EnhancedAlfrescoCoreAdminHandler extends AlfrescoCoreAdminHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnhancedAlfrescoCoreAdminHandler.class);

    public EnhancedAlfrescoCoreAdminHandler() {
        super();
    }

    public EnhancedAlfrescoCoreAdminHandler(CoreContainer coreContainer) {
        super(coreContainer);
    }

    /**
     * This method implements additional actions to default SOLR Admin REST API
     *
     * @param req API Request including the core name (like alfresco or archive)
     * @param rsp Values and parameters used by HTTP Client in Solr to connect to Alfresco server
     */
    @Override
    protected void handleCustomAction(SolrQueryRequest req, SolrQueryResponse rsp) {

        SolrParams params = req.getParams();
        String action = ofNullable(params.get(CoreAdminParams.ACTION)).map(String::trim).map(String::toUpperCase).orElse("");
        String coreName = coreName(params);
        if (coreName == null) {
            rsp.add("error", "Parameter 'coreName' is required");
            LOGGER.error("Parameter 'coreName' is required");
            return;
        }
        LOGGER.info("Running action {} for core {} with params {}", action, coreName, params);

        if ("HTTP-CLIENT".equals(action)) {
            handleHttpClientAction(coreName, rsp);
        } else {
            super.handleCustomAction(req, rsp);
        }
    }

    /**
     * Handles the HTTP-CLIENT action by retrieving core properties and resource loader information.
     *
     * <p>This method retrieves core properties and resource loader information for the specified Solr core,
     * completes core properties, and adds repository and Solr details to the SolrQueryResponse.</p>
     *
     * @param coreName The name of the Solr core for which HTTP-CLIENT action is being handled.
     * @param rsp The SolrQueryResponse object to which repository and Solr details will be added.
     */
    private void handleHttpClientAction(String coreName, SolrQueryResponse rsp) {

        CoreDescriptor coreDescriptor = coreContainer.getCoreDescriptor(coreName);
        if (coreDescriptor == null) {
            rsp.add("error", "Core with name '" + coreName + "' not found");
            LOGGER.error("Core with name '{}' not found", coreName);
            return;
        }

        Properties coreProperties = new CoreDescriptorDecorator(coreDescriptor).getProperties();
        SecretSharedPropertyCollector.completeCoreProperties(coreProperties);
        SolrResourceLoader loader = coreContainer.getCore(coreName).getLatestSchema().getResourceLoader();

        HttpClientAction.addRepoDetails(rsp, coreProperties, loader);
        HttpClientAction.addSolrDetails(rsp, coreProperties);

    }
}
