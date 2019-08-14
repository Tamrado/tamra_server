package com.webapp.timeline.security;

import com.webapp.timeline.repository.UsersEntityRepository;
import com.webapp.timeline.web.UsersController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private boolean postOnly = true;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private UsersEntityRepository usersEntityRepository;

    @Autowired
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,UsersEntityRepository usersEntityRepository) {
        super.setAuthenticationManager(authenticationManager);
        this.usersEntityRepository = usersEntityRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.debug("JwtAuthentication.attemptAuthentication ::::");
        if(postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String userId = obtainUsername(request);
        String password = obtainPassword(request);
        if(StringUtils.isEmpty(userId)) {
            userId = "";
        }
        if(StringUtils.isEmpty(password)) {
            password = "";
        }
        userId = userId.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userId, password);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}
