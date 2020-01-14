package com.webapp.timeline.membership.service.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.webapp.timeline.membership.domain.deserializer.KakaoFirstDeserializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonDeserialize(using = KakaoFirstDeserializer.class)
public class KakaoFirstInfo {

    private String uid;
    private String nickname;
    private String thumbnail;
    private String accessToken;
    private String refreshToken;
    private String email;

    public KakaoFirstInfo(String uid, String nickname,String thumbnail,String accessToken, String refreshToken, String email){
        this.uid = uid;
        this.nickname = nickname;
        this.thumbnail = thumbnail;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
    }
}
