<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/admin/admin-common.lib.js">

function main()
{
   // mandatory model values for Admin UI
   model.attributes = [];
   model.tools = Admin.getConsoleTools("admin-search-client");
   model.metadata = Admin.getServerMetaData();
}

main();
