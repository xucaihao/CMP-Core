package com.cmp.core.eip.modle;

import java.util.List;

public class ResEipAddresses {

    private List<EipAddressInfo> eipAddresses;

    public ResEipAddresses() {
    }

    public ResEipAddresses(List<EipAddressInfo> eipAddresses) {
        this.eipAddresses = eipAddresses;
    }

    public List<EipAddressInfo> getEipAddresses() {
        return eipAddresses;
    }

    public void setEipAddresses(List<EipAddressInfo> eipAddresses) {
        this.eipAddresses = eipAddresses;
    }
}
