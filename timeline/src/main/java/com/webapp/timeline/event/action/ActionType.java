package com.webapp.timeline.event.action;

public enum ActionType {

    TAG_CREATE("post.tag.create"),
    TAG_UPDATE("post.tag.update");

    private String code;

    ActionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
