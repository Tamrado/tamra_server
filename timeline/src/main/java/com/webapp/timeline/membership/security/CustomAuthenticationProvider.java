package com.webapp.timeline.membership.security;

import com.amazonaws.services.directory.model.AuthenticationFailedException;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider, Serializable {
    private JwtTokenProvider jwtTokenProvider;
    private UserSignServiceImpl userSignServiceImpl;
    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    public CustomAuthenticationProvider(JwtTokenProvider jwtTokenProvider,UserSignServiceImpl userSignServiceImpl){
        this.jwtTokenProvider = jwtTokenProvider;
        this.userSignServiceImpl = userSignServiceImpl;
    }

    @Override
    public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
        log.debug("CustomAuthenticationProvider.authenticate ::::");
        JwtAuthenticationToken authentication1 = (JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();

         String token = authentication1.getToken();
        if (token != null && jwtTokenProvider.validateExpirationToken(token)) {
            String userId = jwtTokenProvider.extractUserIdFromToken(token).get();
            Users user = null;
            if (!StringUtils.isEmpty(userId)) {
                user = userSignServiceImpl.loadUserByUsername(userId);
            }
            if (ObjectUtils.isEmpty(user))
                throw new UsernameNotFoundException("Invalid username");


            return new JwtAuthenticationToken(token, user, user.getAuthorities());

        }
        else{
            return new JwtAuthenticationToken(null,null,null);
        }
    }
    @Override
    public boolean supports(Class<? extends Object> authentication) throws AuthenticationException {
        log.debug("CustomAuthenticationProvider.supports ::::");
        if(SecurityContextHolder.getContext().getAuthentication() == null) throw new AuthenticationFailedException("fail");
        return true;
    }
}
