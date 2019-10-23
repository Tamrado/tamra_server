package com.webapp.timeline.membership.service.result;

public class ValidationInfo {
    private String objectName;
    private String issue;
    public ValidationInfo(){
        this.objectName = null;
        this.issue = null;
    }
    public ValidationInfo(String objectName,String issue){
        this.objectName = objectName;
        this.issue = issue;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
