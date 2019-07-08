package com.webapp.timeline.domain;

import java.util.ArrayList;

public class Postphotos {
    private int postId;
    private ArrayList<ListVO> photoUrl;
    private int photoId;

    public Postphotos(int postId, ArrayList<ListVO> photoUrl, int photoId) {
        this.postId = postId;
        this.photoUrl = photoUrl;
        this.photoId = photoId;
    }

    public Postphotos() {}

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPostId() {
        return postId;
    }

    public void setphotoURL(ArrayList<ListVO> photoUrl) {
        this.photoUrl = photoUrl;
    }

    public ArrayList<ListVO> getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getPhotoId() {
        return photoId;
    }
}
