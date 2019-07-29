package com.webapp.timeline.security;


import com.webapp.timeline.domain.Users;
import com.webapp.timeline.service.membership.UserServiceImpl;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;

public class CustomAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService userDetailsService;
    private PasswordEncoder encoder;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Users user;
    private String userId;

    @Autowired
    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder encoder) {
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("CustomUserAuthenticationProvider.authenticate :::: {}",authentication.toString());

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)authentication;

        userId = token.getName();

        if(!StringUtils.isEmpty(userId)) {
            user = (Users)userDetailsService.loadUserByUsername(userId);
        }

        if(ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("Invalid username");
        }

        user.setId(user.getId());
        user.setPassword(user.getPassword());

        String password = user.getPassword();

        if(!StringUtils.equals(password, encoder.encode(String.valueOf(token.getCredentials())))) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());

    }


    @Override
    public boolean supports(Class<?> authentication) {
        // TODO Auto-generated method stub
        log.debug("CustomUserAuthenticationProvider.supports ::::");
        return UsernamePasswordAuthenticationToken
                .class.equals(authentication);
    }

}
