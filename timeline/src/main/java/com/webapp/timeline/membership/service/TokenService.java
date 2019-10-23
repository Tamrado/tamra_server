package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.security.JwtTokenProvider;
import com.webapp.timeline.membership.service.result.LoggedInfo;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
public class TokenService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;
    private UserSignService userSignService;
    @Autowired
    public TokenService(JwtTokenProvider jwtTokenProvider,UserService userService, UserSignService userSignService){
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userSignService = userSignService;
    }
    public TokenService(){

    }
    public void removeCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        Cookie[] cookies = httpServletRequest.getCookies();
        try {
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    log.error("쿠키 구워");
                    cookies[i].setMaxAge(0);// 유효시간을 0으로 설정
                    cookies[i].setHttpOnly(true);
                    cookies[i].setPath("/");
                    httpServletResponse.addCookie(cookies[i]); // 응답 헤더에 추가
                }
                httpServletResponse.setStatus(200);
            }
        }catch(Exception e) {
            httpServletResponse.setStatus(404);
        }
    }
    public LoggedInfo addCookie(HttpServletResponse response,String userId){
        Cookie cookie = new Cookie("accesstoken", jwtTokenProvider.createToken(userId));
        cookie.setMaxAge( 60 * 60*24);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return userService.setLoggedInfo(response,userId);
    }
    public LoggedInfo findUserAndAddCookie(HttpServletResponse response, Map<String,Object> user){
        if(userSignService.findUser(user,response))
            return addCookie(response,user.get("id").toString());
        else return null;
    }
    public String sendIdInCookie(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) {
        Cookie[] cookies = httpServletRequest.getCookies();
        httpServletResponse.setStatus(404);
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("accesstoken") && jwtTokenProvider.getExpirationToken(cookies[i].getValue()).getTime() - System.currentTimeMillis() > 0) {
                        String id = jwtTokenProvider.extractUserIdFromToken(cookies[i].getValue());
                        httpServletResponse.setStatus(200);
                        return id;
                }
            }
        }
        return null;
    }
    public void checkCookieAndRenew(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        Cookie[] cookies = httpServletRequest.getCookies();
        httpServletResponse.setStatus(404);
        if(cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("accesstoken")) {
                    if (jwtTokenProvider.getExpirationToken(cookies[i].getValue()).getTime() - System.currentTimeMillis() > 60 * 60 * 6) {
                        String id = jwtTokenProvider.extractUserIdFromToken(cookies[i].getValue());
                        cookies[i].setMaxAge(0);
                        httpServletResponse.addCookie(cookies[i]);
                        Cookie cookie = new Cookie("accesstoken", jwtTokenProvider.createToken(id));
                        cookie.setMaxAge(60 * 60 * 24);
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        httpServletResponse.addCookie(cookie);
                        httpServletResponse.setStatus(200);
                    }
                }
            }
        }
    }
    public LoggedInfo sendInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String id = sendIdInCookie(httpServletRequest,httpServletResponse);
        if(httpServletResponse.getStatus() != 404)
            return userService.setLoggedInfo(httpServletResponse, id);

        else return null;
    }
}
