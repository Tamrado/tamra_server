package com.webapp.timeline.security;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.service.membership.UserSignService;
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
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private static String secret;
    @Value("${spring.jwt.secret}")
    public void setSecret(String secret){
        this.secret = secret;
    }
    final long tokenValidMilisecond = 1000L *60*60;
    private UserSignService userSignService;
    @Autowired
    public JwtTokenProvider(UserSignService userSignService) {
        this.userSignService = userSignService;
    }
    public JwtTokenProvider(){}
    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String userId){
        log.debug("JwtTokenProvider.createToken ::::");
        String accessToken;
        Claims claims;
        Date expiration;
        claims = Jwts.claims().setSubject(userId);
        expiration = new Date(System.currentTimeMillis() + tokenValidMilisecond);
        accessToken = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secret)
                .setHeaderParam("typ","JWT")
                .setClaims(claims)
                .setIssuedAt(new java.sql.Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .compact();

        return accessToken;
    }
    public String extractUserIdFromToken(String token) {
        log.info("JwtTokenProvider.extractUserIdFromToken ::::");
        try {
            log.info(Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject());
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String resolveToken(HttpServletRequest req){
        log.info("JwtTokenProvider.resolveToken ::::");
        log.info(req.getHeader("X-AUTH-TOKEN"));
        return req.getHeader("X-AUTH-TOKEN");
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
    // Jwt 토큰으로 인증 정보를 조회
    public Authentication getAuthentication(String token) {
        log.info("JwtTokenProvider.getAuthentication ::::");
        String userId = this.extractUserIdFromToken(token);
        Users user = null;
        if(!StringUtils.isEmpty(userId)) {
           user = userSignService.loadUserByUsername(userId);
        }
        if(ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("Invalid username");
        }
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

}
