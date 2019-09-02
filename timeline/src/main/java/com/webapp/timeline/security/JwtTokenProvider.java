package com.webapp.timeline.security;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.service.membership.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@PropertySource("classpath:/jwt.yml")
@Component
public class JwtTokenProvider {

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("jwt.secretKey")
    private String secret;
    private UserServiceImpl userServiceImpl;
    private final long tokenValidMilisecond = 1000L *60*60;
    private String jws;
    private Claims claims;
    private Date expiration;
    private Users user;
    private String userId;

    @Autowired
    public JwtTokenProvider(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }
    public JwtTokenProvider(){

    }

    public String createToken(String userId,String password){
        Key tokenKey = MacProvider.generateKey(SignatureAlgorithm.HS256);
        log.debug("JwtTokenProvider.createToken ::::");
        log.error(secret);
        claims = Jwts.claims().setSubject(userId);
        claims.put("password",password);
        expiration = new Date(System.currentTimeMillis() + tokenValidMilisecond);
        jws = Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setClaims(claims)
                .setIssuedAt(new java.sql.Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, tokenKey)
                .compact();
        return jws;
    }
    public String extractUserIdFromToken(String token) {
        log.debug("JwtTokenProvider.extractUserIdFromToken ::::");
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            return null;
        }
    }


    public String resolveToken(HttpServletRequest req){
        log.debug("JwtTokenProvider.resolveToken ::::");
        return req.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateExpirationToken(String jwtToken) {
        log.debug("JwtTokenProvider.validateExpirationToken ::::");
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
        log.debug("JwtTokenProvider.getAuthentication ::::");
        userId = this.extractUserIdFromToken(token);

        if(!StringUtils.isEmpty(userId)) {
            user = userServiceImpl.loadUserByUsername(userId);
        }

        if(ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("Invalid username");
        }
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

}
