package com.webapp.timeline.membership.service.interfaces;

import com.webapp.timeline.membership.domain.RefreshToken;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.response.KakaoFirstInfo;
import com.webapp.timeline.membership.service.response.KakaoSecondInfo;
import com.webapp.timeline.membership.service.response.LoggedInfo;

import javax.servlet.http.HttpServletResponse;

public interface UserKakaoSignService {
    LoggedInfo login(KakaoFirstInfo kakaoFirstInfo, HttpServletResponse httpServletResponse) throws RuntimeException;
    void firstSignUp(KakaoFirstInfo kakaoFirstInfo, HttpServletResponse httpServletResponse) throws RuntimeException;
    LoggedInfo loginNext(KakaoSecondInfo kakaoSecondInfo,Long id) throws RuntimeException;
    LoggedInfo secondSignUp(String uid,KakaoSecondInfo kakaoSecondInfo) throws RuntimeException;
    void saveRefreshToken(String uid,String token) throws RuntimeException;
    void makeKakaoCookie(HttpServletResponse httpServletResponse,String accesstoken);
    Boolean isUserTrue(String uid) throws RuntimeException;
}
