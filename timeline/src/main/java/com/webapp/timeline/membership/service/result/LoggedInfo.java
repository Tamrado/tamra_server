package com.webapp.timeline.membership.service.result;

public class LoggedInfo {
    private String thumbnail;
    private String username;

    public LoggedInfo(){

    }
    public LoggedInfo(String username, String thumbnail){
        this.thumbnail = thumbnail;
        this.username = username;
    }
    public void setThumbnail(String thumbnail){
        this.thumbnail = thumbnail;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getThumbnail(){
        return thumbnail;
    }
    public String getUsername(){
        return username;
    }

}
