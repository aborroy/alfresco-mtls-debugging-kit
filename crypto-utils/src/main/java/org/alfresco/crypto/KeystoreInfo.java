package org.alfresco.crypto;

import java.util.List;

public class KeystoreInfo {

    public String status;
    public List<AliasDetails> aliasDetailsList;
    public List<AliasExistence> aliasExistenceList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AliasDetails> getAliasDetailsList() {
        return aliasDetailsList;
    }

    public void setAliasDetailsList(List<AliasDetails> aliasDetailsList) {
        this.aliasDetailsList = aliasDetailsList;
    }

    public List<AliasExistence> getAliasExistenceList() {
        return aliasExistenceList;
    }

    public void setAliasExistenceList(List<AliasExistence> aliasExistenceList) {
        this.aliasExistenceList = aliasExistenceList;
    }

}
