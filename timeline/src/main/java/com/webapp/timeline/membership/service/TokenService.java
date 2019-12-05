package com.webapp.timeline.membership.service;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
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
    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;
    private UserSignService userSignService;
    @Autowired
    public TokenService(JwtTokenProvider jwtTokenProvider, UserService userService, UserSignService userSignService){
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userSignService = userSignService;
    }
    public TokenService(){

    }
    public void removeCookie(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws RuntimeException{
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("accesstoken")) {
                    cookies[i].setMaxAge(0);// 유효시간을 0으로 설정
                    cookies[i].setHttpOnly(true);
                    cookies[i].setPath("/");
                    httpServletResponse.addCookie(cookies[i]); // 응답 헤더에 추가
                    }
                }
            }
    }
    public LoggedInfo addCookie(HttpServletResponse response,String userId) throws RuntimeException{
        String accesstoken = jwtTokenProvider.createToken(userId);
        Cookie cookie;
        cookie = new Cookie("accesstoken", accesstoken);
        cookie.setMaxAge( 60 * 60*24);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return userService.setLoggedInfo(userId);
    }
    public LoggedInfo findUserAndAddCookie(Map<String,Object> user,HttpServletResponse response) throws RuntimeException{
        userSignService.findUser(user);
        return addCookie(response,user.get("id").toString());
    }
    public String sendIdInCookie(HttpServletRequest httpServletRequest) throws RuntimeException{
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("accesstoken") && jwtTokenProvider.getExpirationToken(cookies[i].getValue()).getTime() - System.currentTimeMillis() > 0) {
                        String id = jwtTokenProvider.extractUserIdFromToken(cookies[i].getValue());
                        return id;
                }
            }
        }
        throw new NoInformationException();
    }
    public void checkCookieAndRenew(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws RuntimeException{
        Cookie[] cookies = httpServletRequest.getCookies();
        Boolean accesstokenTrue = false;
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
                        accesstokenTrue = true;
                    }
                }
            }
            if(!accesstokenTrue) throw new NoInformationException();
        }
        else throw new NoInformationException();
    }
    public LoggedInfo sendInfo(String userId,HttpServletRequest httpServletRequest) throws RuntimeException{
        String id = sendIdInCookie(httpServletRequest);
        if(id.equals(userId))
            return userService.setLoggedInfo(id);
        else throw new NoMatchPointException();
    }
}
