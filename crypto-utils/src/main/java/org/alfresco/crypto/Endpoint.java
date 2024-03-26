package org.alfresco.crypto;

import java.util.List;

public class Endpoint {

    String tlsProcotol;
    List<TrustedCertificate> trustedCertificates;

    public String getTlsProcotol() {
        return tlsProcotol;
    }

    public void setTlsProcotol(String tlsProcotol) {
        this.tlsProcotol = tlsProcotol;
    }

    public List<TrustedCertificate> getTrustedCertificates() {
        return trustedCertificates;
    }

    public void setTrustedCertificates(List<TrustedCertificate> trustedCertificates) {
        this.trustedCertificates = trustedCertificates;
    }

}
