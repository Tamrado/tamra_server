package com.webapp.timeline.membership.security;

import com.google.gson.Gson;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.membership.service.response.KakaoTimeInfo;
import com.webapp.timeline.membership.service.response.UserIdInfo;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;
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

    private static String isExpiredTokenUrl;
    private static String getUserInfoUrl;
    @Value("${social.url.isExpiredToken}")
    public void setIsExpiredTokenUrl(String isExpiredTokenUrl) {this.isExpiredTokenUrl = isExpiredTokenUrl;};
    @Value("${social.url.getUserInfo}")
    public void setGetUserInfoUrl(String getUserInfoUrl) { this.getUserInfoUrl = getUserInfoUrl;}


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
    public String extractUserIdFromAccessToken(String token) throws RuntimeException {
        log.info("JwtTokenProvider.extractUserIdFromAccessToken ::::");
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
           return null;
        }
    }
    public String extractUserIdFromKakaoToken(String token) throws RuntimeException{
        log.info("JwtTokenProvider.extractUserIdFromKakaoToken ::::");
        ResponseEntity<String> responseEntity = this.getUserInfoKakaoAPI(token);
        if(responseEntity == null) return null;
          UserIdInfo idInfo = gson.fromJson(responseEntity.getBody(), UserIdInfo.class);
          return idInfo.getId().toString() + "Kakao";
    }

    public Long getTokenExpiresInMillis(HttpServletRequest request) throws RuntimeException{
        log.info("JwtToken.getTokenExpiresInMillis :::");
        if(this.isExpiredTokenKakaoAPI(this.resolveKakaoCookie(request)))
            return  Long.parseLong("4000");
        else
            return Long.parseLong("0");
    }
    public String resolveKakaoCookie(HttpServletRequest request){
        log.info("JwtToken.resolveKakaoCookie :::");
        Cookie[] cookies = request.getCookies();
       return Arrays.asList(cookies).stream()
                .filter(item -> item.getName().equals("kakaoAccesstoken"))
                .iterator().next().getValue();

    }
    private HttpHeaders makeHeader(String token){
        log.info("JwtToken.makeHeader :::");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer "
                + token);
        return headers;
    }
    public Boolean isExpiredTokenKakaoAPI(String token) throws RuntimeException{
        log.info("JwtTokenProvider:::: isExpiredTokenKakaoAPI");
        try {
             ResponseEntity<String> responseEntity = restTemplate.exchange(isExpiredTokenUrl, HttpMethod.GET,
                    new HttpEntity<String>(this.makeHeader(token)), String.class);
             if(responseEntity.getStatusCode() == HttpStatus.OK)
                 return true;
        }catch(Exception e){
        }
        return false;
    }

    public ResponseEntity<String> getUserInfoKakaoAPI(String token) throws RuntimeException{
        log.info("JwtTokenProvider:::: getUserInfoKakaoAPI");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("property_keys","[\"id\"]");
        try {
             return restTemplate.postForEntity(getUserInfoUrl, new HttpEntity<>(
                    params,
                    this.makeHeader(token)), String.class);
        }catch(Exception e){
            throw new NoInformationException();
        }
    }
    public String resolveToken(HttpServletRequest request){
        log.info("JwtTokenProvider.resolveToken ::::");
        List<Cookie> cookieList = Arrays.asList(request.getCookies());
        return extractTokenValue(cookieList,request);
    }
    public Stream<Cookie> makeBasicCookieStream(List<Cookie> cookieList){
        log.info("JwtToken.makeBasicCookieStream :::");
        return this.checkIsToken("accesstoken",
                this.getExpirationToken(cookieList).getTime() - System.currentTimeMillis(),cookieList);
    }
    public Stream<Cookie> makeKakaoCookieStream(List<Cookie> cookieList,HttpServletRequest request){
        log.info("JwtToken.makeKakaoCookieStream :::");
        return this.checkIsToken("kakaoAccesstoken"
                ,this.getTokenExpiresInMillis(request),cookieList);
    }
    public String extractTokenValue(List<Cookie> cookieList,HttpServletRequest request){
        log.info("JwtToken.extractTokenValue :::");
        return makeBasicCookieStream(cookieList).count() > 0 ?
                        makeBasicCookieStream(cookieList).iterator().next().getValue() :
                (makeKakaoCookieStream(cookieList, request).count() > 0 ?
                        makeKakaoCookieStream(cookieList, request)
                                .iterator().next().getValue() :
                        null);
    }
    public Date getExpirationToken(List<Cookie> cookieList){
        log.info("JwtToken.getExpirationToken :::");
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
    public boolean validateExpirationKakaoToken(String jwtToken){
        log.info("JwtTokenProvider.validateExpirationKakaoToken ::::");
        if(this.isExpiredTokenKakaoAPI(jwtToken))
            return true;
        else
            return false;
    }
    public boolean validateExpirationAccessToken(String jwtToken) {
        log.info("JwtTokenProvider.validateExpirationAccessToken ::::");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (Exception e) {
            return false;
        }
    }
    public Stream<Cookie> checkIsToken(String name,Long time,List<Cookie> cookieList){
        log.info("JwtToken.checkIsToken :::");
        return cookieList.stream()
                .filter(cookie->cookie.getName().equals(name))
                .filter(cookie-> time > 0);
    }

}
