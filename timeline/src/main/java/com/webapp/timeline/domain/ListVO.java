package com.webapp.timeline.domain;

public class ListVO {
    private int id;
    private String url;

    public ListVO(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public ListVO() {}

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
