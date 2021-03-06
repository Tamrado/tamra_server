package com.webapp.timeline.membership.service.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggedInfo {
    private String thumbnail;
    private String username;
    private String nickname;
    private String comment;
    private String category;
    public LoggedInfo(){

    }
    public LoggedInfo(String username, String thumbnail,String nickname,String comment,String category){
        this.thumbnail = thumbnail;
        this.username = username;
        this.nickname = nickname;
        this.comment = comment;
        this.category = category;
    }

}
