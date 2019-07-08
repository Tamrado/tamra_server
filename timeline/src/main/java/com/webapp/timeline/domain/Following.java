package com.webapp.timeline.domain;

import java.sql.Date;

public class Following {
    private int masterId;
    private String followId;
    private int isAccepted;
    private Date timestamp;

    public Following(int masterId, String followId, int isAccepted, Date timestamp) {
        this.masterId = masterId;
        this.followId = followId;
        this.isAccepted = isAccepted;
        this.timestamp = timestamp;
    }

    public Following() {}

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setFollowId(String followId) {
        this.followId = followId;
    }

    public String getFollowId() {
        return followId;
    }

    public void setIsAccepted(int isAccepted) {
        this.isAccepted = isAccepted;
    }

    public int getIsAccepted() {
        return isAccepted;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

