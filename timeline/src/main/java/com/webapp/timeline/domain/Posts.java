package com.webapp.timeline.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import javax.persistence.*;

@Entity
@Table(name = "posts")
public class Posts {
    @EmbeddedId
    private PostId id;

    private String content;

    private int showLevel;

    private Timestamp lastUpdate;

    private ArrayList<ListVO> photoUrl;

    private int photoId;

    public Posts(PostId id, String content, int showLevel, Timestamp lastUpdate, ArrayList<ListVO> photoUrl, int photoId) {
        this.id = id;
        this.content = content;
        this.showLevel = showLevel;
        this.lastUpdate = lastUpdate;
        this.photoUrl = photoUrl;
        this.photoId = photoId;
    }

    public Posts() {}

    public void setid(PostId id) {
        this.id = id;
    }

    public PostId getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setShowLevel(int showLevel) {
        this.showLevel = showLevel;
    }

    public int getShowLevel() {
        return showLevel;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setPhotoUrl(ArrayList<ListVO> photoUrl) {
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
