package com.webapp.timeline.membership.service.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoSecondInfo {
    private String email;
    private String comment;

    public KakaoSecondInfo(String email, String comment){
        this.email = email;
        this.comment = comment;
    }
}
