package com.cmp.core.disk.model.res;

import java.util.List;

public class ResDisks {

    private List<ResDiskInfo> disks;

    public ResDisks() {
    }

    public ResDisks(List<ResDiskInfo> disks) {
        this.disks = disks;
    }

    public List<ResDiskInfo> getDisks() {
        return disks;
    }

    public void setDisks(List<ResDiskInfo> disks) {
        this.disks = disks;
    }
}
