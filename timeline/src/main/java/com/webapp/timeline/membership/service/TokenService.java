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
    public TokenService(JwtTokenProvider jwtTokenProvider, UserService userService, UserSignService userSignService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userSignService = userSignService;
    }

    public TokenService() {

    }

    public Cookie makeCookie(String accesstoken, String name) throws RuntimeException {
        Cookie cookie = new Cookie(name, accesstoken);
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    public void removeCookies(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws RuntimeException {
        List<Cookie> cookieList = Arrays.asList(httpServletRequest.getCookies());
        if (cookieList.isEmpty()) return;
        this.removeCookie("accesstoken", cookieList, httpServletResponse,httpServletRequest);
        this.removeCookie("kakaoAccesstoken", cookieList, httpServletResponse,httpServletRequest);
    }

    public LoggedInfo addCookie(HttpServletResponse response, String userId) throws RuntimeException {
        String accesstoken = jwtTokenProvider.createToken(userId);
        response.addCookie(this.makeCookie(accesstoken, "accesstoken"));
        return userService.setLoggedInfo(userId);
    }

    public LoggedInfo findUserAndAddCookie(Map<String, Object> user, HttpServletResponse response) throws RuntimeException {
        userSignService.findUser(user);
        return addCookie(response, user.get("id").toString());
    }

    public String sendIdInCookie(String name, HttpServletRequest httpServletRequest) throws RuntimeException {
        log.info("TokenService.sendIdInCookie::::");
        List<Cookie> cookieList = Arrays.asList(httpServletRequest.getCookies());
        if (cookieList.isEmpty()) throw new NoInformationException();
        if (this.makeStreamForName(name,cookieList,httpServletRequest).count() == 0) return null;
        log.error("TokenService.sendIdInCookie:::: return 전");
        return Optional.ofNullable(jwtTokenProvider.extractUserIdFromAccessToken(
                jwtTokenProvider.makeBasicCookieStream(cookieList)
                .iterator().next().getValue())).orElseGet(()->jwtTokenProvider.extractUserIdFromKakaoToken(
                        jwtTokenProvider.resolveKakaoCookie(httpServletRequest)));
    }

    public void checkCookieAndRenew(String name,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws RuntimeException {
        List<Cookie> cookieList = Arrays.asList(httpServletRequest.getCookies());

        if (cookieList.isEmpty()) throw new NoStoringException();
        if (this.makeStreamForName(name,cookieList,httpServletRequest).count() == 0) throw new NoMatchPointException();

        this.cookieRenew(name,cookieList, httpServletResponse,httpServletRequest);
    }

    private void cookieRenew(String name,List<Cookie> cookieList, HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest) {
        this.makeStreamForName(name,cookieList,httpServletRequest)
                .forEach(cookie -> {
                    String id = jwtTokenProvider.extractUserIdFromAccessToken(cookie.getValue());
                    cookie.setMaxAge(0);
                    httpServletResponse.addCookie(cookie);
                    httpServletResponse.addCookie(this.makeCookie(jwtTokenProvider.createToken(id), "accesstoken"));
                });
    }

    private void removeCookie(String name, List<Cookie> cookieList, HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest) {
            this.makeStreamForName(name,cookieList,httpServletRequest)
                .forEach(cookie -> {
                    cookie.setMaxAge(0);
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    httpServletResponse.addCookie(cookie);
                });
    }

    public LoggedInfo sendInfo(String userId, HttpServletRequest httpServletRequest) throws RuntimeException {
        String name = this.sendTokenName(userId);
        String id = Optional.ofNullable(sendIdInCookie(name, httpServletRequest))
                .orElseThrow(() -> new NoMatchPointException());
        log.error(id);
        if (id.equals(userId))
            return userService.setLoggedInfo(id);
        else throw new NoMatchPointException();
    }
    public Stream<Cookie> makeStreamForName(String name,List<Cookie> cookieList,HttpServletRequest request) throws RuntimeException{
        if(name.equals("kakaoAccesstoken"))
            return jwtTokenProvider.makeKakaoCookieStream(cookieList, request);
        return jwtTokenProvider.makeBasicCookieStream(cookieList);

    }
    public String sendTokenName(String userId){
        if(userId.contains("Kakao")) return "KakaoAccesstoken";
        else return "accesstoken";
    }

}
