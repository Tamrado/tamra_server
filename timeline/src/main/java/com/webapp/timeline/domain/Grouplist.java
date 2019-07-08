package com.webapp.timeline.domain;

public class Grouplist {
    private int groupId;
    private int category;
    private String groupName;

    public Grouplist(int groupId, int category, String groupName) {
        this.groupId = groupId;
        this.category = category;
        this.groupName = groupName;
    }

    public Grouplist() {}

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getCategory() {
        return category;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
