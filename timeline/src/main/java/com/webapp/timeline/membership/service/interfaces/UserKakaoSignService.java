package com.webapp.timeline.membership.service.interfaces;

import com.webapp.timeline.membership.domain.RefreshToken;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.response.KakaoFirstInfo;
import com.webapp.timeline.membership.service.response.KakaoSecondInfo;
import com.webapp.timeline.membership.service.response.LoggedInfo;

import javax.servlet.http.HttpServletResponse;

public interface UserKakaoSignService {
    void login(KakaoFirstInfo kakaoFirstInfo, HttpServletResponse httpServletResponse) throws RuntimeException;
    void firstSignUp(KakaoFirstInfo kakaoFirstInfo, HttpServletResponse httpServletResponse) throws RuntimeException;
    LoggedInfo loginNext(KakaoSecondInfo kakaoSecondInfo,Long id) throws RuntimeException;
    LoggedInfo secondSignUp(Users user, KakaoSecondInfo kakaoSecondInfo) throws RuntimeException;
    void saveRefreshToken(RefreshToken refreshToken) throws RuntimeException;
}
