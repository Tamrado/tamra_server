package com.webapp.timeline.membership.service.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface KakaoService {

    void refreshExpiredKakaoToken(String uid,HttpServletResponse response) throws RuntimeException;
    void isExpiredTokenAndGetTokenInfo(String uid,HttpServletRequest request,HttpServletResponse response) throws RuntimeException;
}
