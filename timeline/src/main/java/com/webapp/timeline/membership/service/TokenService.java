package com.webapp.timeline.membership.service;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.membership.security.JwtAuthenticationToken;
import com.webapp.timeline.membership.security.JwtTokenProvider;
import com.webapp.timeline.membership.service.interfaces.UserService;
import com.webapp.timeline.membership.service.interfaces.UserSignService;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import org.apache.catalina.filters.ExpiresFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Stream;

@Service
public class TokenService {
    Logger log = LoggerFactory.getLogger(this.getClass());
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
    public Cookie makeCookie(String accesstoken,String name) throws RuntimeException{
        Cookie cookie = new Cookie(name,accesstoken);
        cookie.setMaxAge( 60 * 60*24);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
    public void removeCookies(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws RuntimeException{
        List<Cookie> cookieList = Arrays.asList(httpServletRequest.getCookies());
        if(cookieList.isEmpty()) return;
        this.removeCookie("accesstoken",cookieList,httpServletResponse);
        this.removeCookie("kakaoAccesstoken",cookieList,httpServletResponse);
    }
    public LoggedInfo addCookie(HttpServletResponse response,String userId) throws RuntimeException{
        String accesstoken = jwtTokenProvider.createToken(userId);
        response.addCookie(this.makeCookie(accesstoken,"accesstoken"));
        return userService.setLoggedInfo(userId);
    }
    public LoggedInfo findUserAndAddCookie(Map<String,Object> user,HttpServletResponse response) throws RuntimeException{
        userSignService.findUser(user);
        return addCookie(response,user.get("id").toString());
    }
    public Optional<String> sendIdInCookie(String name, HttpServletRequest httpServletRequest) throws RuntimeException {
        List<Cookie> cookieList = Arrays.asList(httpServletRequest.getCookies());

        if (cookieList.isEmpty()) throw new NoInformationException();
        Stream<Cookie> cookieStream = jwtTokenProvider.checkIsToken(name,cookieList);
        if(cookieStream.count() == 0) return null;
        return jwtTokenProvider.extractUserIdFromToken(cookieStream.iterator().next().getValue());
    }

    public void checkCookieAndRenew(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws RuntimeException{
        List<Cookie> cookieList = Arrays.asList(httpServletRequest.getCookies());

        if(cookieList.isEmpty()) throw new NoStoringException();
        if(jwtTokenProvider.checkIsToken("accesstoken",cookieList).count() == 0) throw new NoMatchPointException();

        this.cookieRenew(cookieList,httpServletResponse);
    }

    private void cookieRenew(List<Cookie> cookieList,HttpServletResponse httpServletResponse){
        jwtTokenProvider.checkIsToken("accesstoken",cookieList)
                .forEach(cookie->{
                    Optional<String> id = jwtTokenProvider.extractUserIdFromToken(cookie.getValue());
                    cookie.setMaxAge(0);
                    httpServletResponse.addCookie(cookie);
                    httpServletResponse.addCookie(this.makeCookie(jwtTokenProvider.createToken(id.get()),"accesstoken"));
                });
    }
    private void removeCookie(String name, List<Cookie> cookieList, HttpServletResponse httpServletResponse) {
        jwtTokenProvider.checkIsToken(name, cookieList)
                .forEach(cookie -> {
                    cookie.setMaxAge(0);
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    httpServletResponse.addCookie(cookie);
                });
    }

    public LoggedInfo sendInfo(String userId,HttpServletRequest httpServletRequest) throws RuntimeException{
        String name = userService.sendTokenCategory(userId);
        Optional<String> id = sendIdInCookie(name,httpServletRequest);
        id.orElseThrow(()-> new NoMatchPointException());
        if(id.equals(userId))
            return userService.setLoggedInfo(id.get());
        else throw new NoMatchPointException();
    }
}
