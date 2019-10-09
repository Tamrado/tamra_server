package com.webapp.timeline.domain;

public class PhotoVO {
    private int photoId;
    private String url;

    public PhotoVO(int photoId, String url) {
        this.photoId = photoId;
        this.url = url;
    }

    public PhotoVO() {}

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
