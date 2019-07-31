package com.webapp.timeline.security;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.service.membership.CommonResult;
import com.webapp.timeline.service.membership.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("spring.jwt.secret")
    private String secretKey;
    private UserServiceImpl userServiceImpl;
    private PasswordEncoder encoder;
    private JwtTokenProvider jwtTokenProvider;
    private final long tokenValidMilisecond = 1000L *60*60;
    private String jws;
    private Claims claims;
    private Date now;
    private Date expiration;
    private Users user;
    private String userId;
    @Autowired
    public JwtTokenProvider(UserServiceImpl userServiceImpl, PasswordEncoder encoder, JwtTokenProvider jwtTokenProvider) {
        this.userServiceImpl = userServiceImpl;
        this.encoder = encoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String userId, List<String> authority){
        log.debug("JwtTokenProvider.createToken ::::");
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
        log.debug("JwtTokenProvider.extractUserIdFromToken ::::");
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
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
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
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
