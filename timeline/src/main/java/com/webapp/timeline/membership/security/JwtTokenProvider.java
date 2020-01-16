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
        if(responseEntity.getStatusCode() == HttpStatus.OK){
          UserIdInfo idInfo = gson.fromJson(responseEntity.getBody(), UserIdInfo.class);
          log.info(idInfo.getId().toString() + "Kakao");
          return idInfo.getId().toString() + "Kakao";
        }
        else return null;
    }

    public Long getTokenExpiresInMillis(HttpServletRequest request) throws RuntimeException{
        ResponseEntity<String> responseEntity = this.isExpiredTokenKakaoAPI(this.resolveKakaoCookie(request));
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
    private HttpHeaders makeHeader(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer "
                + token);
        return headers;
    }
    public ResponseEntity<String> isExpiredTokenKakaoAPI(String token) throws RuntimeException{
        log.info("JwtTokenProvider:::: isExpiredTokenKakaoAPI");
        return restTemplate.exchange(isExpiredTokenUrl, HttpMethod.GET,
                new HttpEntity<String>(this.makeHeader(token)), String.class);
    }

    public ResponseEntity<String> getUserInfoKakaoAPI(String token) throws RuntimeException{
        log.info("JwtTokenProvider:::: getUserInfoKakaoAPI");
        log.error(token);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("property_keys","[\"id\"]");
        return restTemplate.postForEntity(getUserInfoUrl,new HttpEntity<>(
              params,
                this.makeHeader(token)),String.class);
    }
    public String resolveToken(HttpServletRequest request){
        log.info("JwtTokenProvider.resolveToken ::::");
        List<Cookie> cookieList = Arrays.asList(request.getCookies());
        return extractTokenValue(cookieList,request);
    }
    public Stream<Cookie> makeBasicCookieStream(List<Cookie> cookieList){
        return this.checkIsToken("accesstoken",
                this.getExpirationToken(cookieList).getTime() - System.currentTimeMillis(),cookieList);
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
    public boolean validateExpirationKakaoToken(String jwtToken){
        log.info("JwtTokenProvider.validateExpirationKakaoToken ::::");
        ResponseEntity<String> responseEntity = this.isExpiredTokenKakaoAPI(jwtToken);
        if(responseEntity.getStatusCode() == HttpStatus.OK)
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
        return cookieList.stream()
                .filter(cookie->cookie.getName().equals(name))
                .filter(cookie-> time > 0);
    }

}
