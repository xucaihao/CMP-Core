package com.cmp.core.image.model.res;

public class ResImage {

    private ResImageInfo image;

    public ResImage() {
    }

    public ResImage(ResImageInfo image) {
        this.image = image;
    }

    public ResImageInfo getImage() {
        return image;
    }

    public void setImage(ResImageInfo image) {
        this.image = image;
    }
}
