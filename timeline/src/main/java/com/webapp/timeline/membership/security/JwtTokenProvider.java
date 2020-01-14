package com.webapp.timeline.membership.security;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Stream;

@Component
public class JwtTokenProvider {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private static String secret;
    @Value("${spring.jwt.secret}")
    public void setSecret(String secret){
        this.secret = secret;
    }
    final long tokenValidMilisecond = 1000L *60*60*24;
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
    public Optional<String> extractUserIdFromToken(String token) throws RuntimeException {
        log.info("JwtTokenProvider.extractUserIdFromToken ::::");
        try {
            return Optional.of(Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject());
        } catch (Exception e) {
            throw new NoInformationException();
        }
    }

    public String resolveToken(HttpServletRequest httpServletRequest){
        log.info("JwtTokenProvider.resolveToken ::::");
        List<Cookie> cookieList = Arrays.asList(httpServletRequest.getCookies());
        Stream<Cookie> kakaoCookieStream = this.checkIsToken("kakaoAccesstoken",cookieList);
        Stream<Cookie> basicCookieStream = this.checkIsToken("accesstoken",cookieList);
        return extractTokenValue(kakaoCookieStream,basicCookieStream,cookieList);
    }

    public String extractTokenValue(Stream<Cookie> kakaoCookieStream,Stream<Cookie> basicCookieStream,List<Cookie> cookieList){
        return kakaoCookieStream.count() > 0 ?
                this.checkIsToken("kakaoAccesstoken",cookieList).iterator().next().getValue() :
                (basicCookieStream.count() > 0 ?
                        this.checkIsToken("accesstoken",cookieList).iterator().next().getValue() :
                        null);
    }
    public Date getExpirationToken(String jwtToken){
        try{
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
    public Stream<Cookie> checkIsToken(String name,List<Cookie> cookieList){
        return cookieList.stream()
                .filter(cookie->cookie.getName().equals(name))
                .filter(cookie->this.getExpirationToken(cookie.getValue()).getTime() - System.currentTimeMillis() > 0);
    }

}
