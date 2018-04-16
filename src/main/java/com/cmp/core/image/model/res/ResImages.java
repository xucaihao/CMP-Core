package com.cmp.core.image.model.res;

import java.util.List;

public class ResImages {

    private List<ResImageInfo> images;

    public ResImages() {
    }

    public ResImages(List<ResImageInfo> images) {
        this.images = images;
    }

    public List<ResImageInfo> getImages() {
        return images;
    }

    public void setImages(List<ResImageInfo> images) {
        this.images = images;
    }
}
