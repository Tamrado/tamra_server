package com.webapp.timeline.security;

import com.webapp.timeline.service.membership.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("spring.jwt.secret")
    private String secretKey;
    private final long tokenValidMilisecond = 1000L *60*60;
    private String jws;
    private Claims claims;
    private Date now;
    private Date expiration;

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String userId, List<String> authority){
        claims = Jwts.claims().setSubject(userId);
        claims.put("typ","JWT");
        now = new Date();
        expiration = new Date(now.getTime() + tokenValidMilisecond);
        jws = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return jws;
    }
    public String extractUserIdFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String resolveToken(HttpServletRequest req){
        return req.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateExpirationToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (Exception e) {
            return false;
        }
    }

}
