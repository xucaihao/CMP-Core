package com.cmp.core.region.model;

import java.util.List;

public class ResRegions {

    private List<RegionEntity> regions;

    public ResRegions() {
    }

    public ResRegions(List<RegionEntity> regions) {
        this.regions = regions;
    }

    public List<RegionEntity> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionEntity> regions) {
        this.regions = regions;
    }
}
