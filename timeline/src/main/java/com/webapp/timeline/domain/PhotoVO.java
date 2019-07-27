package com.webapp.timeline.domain;

public class PhotoVO {
    private int id;
    private String url;

    public PhotoVO(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public PhotoVO() {}

    public void setPhotoId(int photoId) {
        this.id = photoId;
    }

    public int getPhotoId() {
        return id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
