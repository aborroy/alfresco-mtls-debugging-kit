package org.alfresco.crypto;

import java.util.List;

public class Endpoint {

    List<String> supportedProcotols;
    List<TrustedCertificate> trustedCertificates;

    public List<String> getSupportedProcotols() {
        return supportedProcotols;
    }

    public void setSupportedProcotols(List<String> supportedProcotols) {
        this.supportedProcotols = supportedProcotols;
    }

    public List<TrustedCertificate> getTrustedCertificates() {
        return trustedCertificates;
    }

    public void setTrustedCertificates(List<TrustedCertificate> trustedCertificates) {
        this.trustedCertificates = trustedCertificates;
    }

}
