package com.webapp.timeline.membership.security;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CookieAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    Logger log = LoggerFactory.getLogger(this.getClass());
    private JwtTokenProvider jwtTokenProvider;
    private UserSignServiceImpl userSignServiceImpl;

    public CookieAuthenticationFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
        setAuthenticationManager( super.getAuthenticationManager() );

    }
    public void setUserSignServiceImpl(UserSignServiceImpl userSignServiceImpl){
        this.userSignServiceImpl = userSignServiceImpl;
    }
    public void setJwtTokenProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("CookieAuthenticationFilter.attemptAuthentication ::::");
        String token = jwtTokenProvider.resolveToken(request);
        if(request.getRequestURI().matches(".*/membership/api/member.*"))
            return new JwtAuthenticationToken(null,null,null);
        log.error("dgsdgdgdgdgsdgdgas");
        if (token != null && jwtTokenProvider.validateExpirationToken(token)) {
            String userId = jwtTokenProvider.extractUserIdFromToken(token);
            Users user = null;
            if(!StringUtils.isEmpty(userId)) {
                user = userSignServiceImpl.loadUserByUsername(userId);
            }
            if(ObjectUtils.isEmpty(user)) {
                throw new UsernameNotFoundException("Invalid username");
            }
            return new JwtAuthenticationToken(token,null,null);
        }
        else throw new UsernameNotFoundException("no user");
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        log.info("CookieAuthenticationFilter.doFilter ::::");
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        try{
            this.attemptAuthentication(request,response);
            chain.doFilter(req,res);
        }catch(AuthenticationException e){
            unsuccessfulAuthentication(request,response,e);
        }

    }

}
