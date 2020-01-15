package com.webapp.timeline.membership.security;

import com.google.gson.Gson;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.membership.service.response.KakaoTimeInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Stream;

@Component
public class JwtTokenProvider {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();
    private static String secret;
    @Value("${spring.jwt.secret}")
    public void setSecret(String secret){
        this.secret = secret;
    }
    final long tokenValidMilisecond = 1000L *60*60*24;

    @Value("${social.url.isExpiredToken}")
    private static String isExpiredTokenUrl;

    public JwtTokenProvider(){}
    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String userId) throws RuntimeException{
        log.debug("JwtTokenProvider.createToken ::::");
        if(userId == null) throw new NoInformationException();
        Claims claims = Jwts.claims().setSubject(userId);
        Date expiration = new Date(System.currentTimeMillis() + tokenValidMilisecond);
        String accessToken = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secret)
                .setHeaderParam("typ","JWT")
                .setClaims(claims)
                .setIssuedAt(new java.sql.Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .compact();

        return accessToken;
    }
    public String extractUserIdFromToken(String token) throws RuntimeException {
        log.info("JwtTokenProvider.extractUserIdFromToken ::::");
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            throw new NoInformationException();
        }
    }

    public Long getTokenExpiresInMillis(HttpServletRequest request) throws RuntimeException{
        ResponseEntity<String> responseEntity = this.isExpiredTokenKakaoAPI(request);
        if(responseEntity.getStatusCode() == HttpStatus.OK)
            return  gson.fromJson(responseEntity.getBody(), KakaoTimeInfo.class).getExpiresInMillis();
        else
            return Long.parseLong("0");
    }
    public String resolveKakaoCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
       return Arrays.asList(cookies).stream()
                .filter(item -> item.getName().equals("kakaoAccesstoken"))
                .iterator().next().getValue();

    }
    public ResponseEntity<String> isExpiredTokenKakaoAPI(HttpServletRequest request) throws RuntimeException{
        log.info("KakaoServiceImpl:::: isExpiredToken");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer "
                + this.resolveKakaoCookie(request));
        try{
            return restTemplate.exchange("https://kapi.kakao.com/v1/user/access_token_info", HttpMethod.GET, new HttpEntity<String>(headers), String.class);
        } catch (Exception e) {
            log.error(e.toString());
            throw new NoMatchPointException();
        }
    }
    public String resolveToken(HttpServletRequest request){
        log.info("JwtTokenProvider.resolveToken ::::");
        List<Cookie> cookieList = Arrays.asList(request.getCookies());
        return extractTokenValue(cookieList,request);
    }
    public Stream<Cookie> makeBasicCookieStream(List<Cookie> cookieList){
        return this.checkIsToken("accesstoken",
                this.getExpirationToken(cookieList).getTime(),cookieList);
    }
    public Stream<Cookie> makeKakaoCookieStream(List<Cookie> cookieList,HttpServletRequest request){
        return this.checkIsToken("kakaoAccesstoken"
                ,this.getTokenExpiresInMillis(request),cookieList);
    }
    public String extractTokenValue(List<Cookie> cookieList,HttpServletRequest request){
        return makeBasicCookieStream(cookieList).count() > 0 ?
                        makeBasicCookieStream(cookieList).iterator().next().getValue() :
                (makeKakaoCookieStream(cookieList, request).count() > 0 ?
                        makeKakaoCookieStream(cookieList, request)
                                .iterator().next().getValue() :
                        null);
    }
    public Date getExpirationToken(List<Cookie> cookieList){
        try{
           String jwtToken = cookieList.stream()
                    .filter(cookie->cookie.getName().equals("accesstoken"))
                    .iterator().next().getValue();
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken);
            return claims.getBody().getExpiration();
        }
        catch(Exception e){
            return new Date();
        }
    }
    public boolean validateExpirationToken(String jwtToken) {
        log.info("JwtTokenProvider.validateExpirationToken ::::");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (Exception e) {
            return false;
        }
    }
    public Stream<Cookie> checkIsToken(String name,Long time,List<Cookie> cookieList){
        return cookieList.stream()
                .filter(cookie->cookie.getName().equals(name))
                .filter(cookie->time - System.currentTimeMillis() > 0);
    }

}
