package com.karpov.vacuum.models;

public class PhotoEditItem {
    private String uri;
    private boolean isLoading;

    public PhotoEditItem(String image) {
        this.uri = image;
    }

    public PhotoEditItem(String image, boolean isLoading) {
        this(image);
        this.isLoading = isLoading;
    }

    public PhotoEditItem() {

    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
