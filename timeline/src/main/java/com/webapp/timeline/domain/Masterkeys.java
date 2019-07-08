package com.webapp.timeline.domain;

public class Masterkeys {
    private int masterId;
    private String userId;

    public Masterkeys(int masterId, String userId) {
        this.masterId = masterId;
        this.userId = userId;
    }

    public Masterkeys() {}

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
