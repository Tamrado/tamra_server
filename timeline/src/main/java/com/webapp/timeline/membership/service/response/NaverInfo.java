package com.webapp.timeline.membership.service.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class NaverInfo {
    private String id;
    private String email;
    private String gender;
    private String nickname;
    private String thumbnail;

    public NaverInfo(String id, String email, String gender, String nickname, String thumbnail){
        this.id = id;
        this.email = email;
        this.gender =gender;
        this.nickname = nickname;
        this.thumbnail = thumbnail;
    }
}
