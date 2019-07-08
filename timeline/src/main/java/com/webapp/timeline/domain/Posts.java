package com.webapp.timeline.domain;

import java.sql.Timestamp;

public class Posts {
    private int postId;
    private int masterId;
    private String content;
    private int showLevel;
    private Timestamp lastUpdate;

    public Posts(int postId, int masterId, String content, int showLevel, Timestamp lastUpdate) {
        this.postId = postId;
        this.masterId = masterId;
        this.content = content;
        this.showLevel = showLevel;
        this.lastUpdate = lastUpdate;
    }

    public Posts() {}

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPostId() {
        return postId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public int getMasterId() {
        return masterId;
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
}
