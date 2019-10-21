package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.security.JwtTokenProvider;
import com.webapp.timeline.membership.service.result.CommonResult;
import com.webapp.timeline.membership.service.result.SingleResult;
import net.bytebuddy.description.field.FieldDescription;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.util.calendar.BaseCalendar;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
public class TokenService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    public TokenService(JwtTokenProvider jwtTokenProvider){
        this.jwtTokenProvider = jwtTokenProvider;
    }
    public TokenService(){

    }
    public CommonResult removeCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        CommonResult commonResult = new CommonResult();
        Cookie[] cookies = httpServletRequest.getCookies();
        try {
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    cookies[i].setMaxAge(0); // 유효시간을 0으로 설정
                    httpServletResponse.addCookie(cookies[i]); // 응답 헤더에 추가
                }
                commonResult.setSuccessResult(200,"success");
            }
        }catch(Exception e){
            commonResult.setFailResult(404,"fail");
        }
        return commonResult;
    }
    public SingleResult<String> addCookie(HttpServletResponse response,SingleResult<String> singleResult){
        Cookie cookie = new Cookie("accesstoken", singleResult.getData());
        cookie.setMaxAge( 60 * 60*24);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return singleResult;
    }
    public CommonResult checkCookieAndRenew(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        CommonResult commonResult = new CommonResult();
        Cookie[] cookies = httpServletRequest.getCookies();
        httpServletResponse.setStatus(404);
        if(cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("accesstoken")) {
                    log.info(Long.toString(System.currentTimeMillis() - jwtTokenProvider.getExpirationToken(cookies[i].getValue()).getTime()));
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
                        commonResult.setSuccessResult(200, "success");
                        return commonResult;
                    }
                }
            }
        }
        return commonResult;
    }
}
