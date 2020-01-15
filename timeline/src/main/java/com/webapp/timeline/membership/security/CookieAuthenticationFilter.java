package com.webapp.timeline.membership.security;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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
import java.util.Optional;

public class CookieAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    Logger log = LoggerFactory.getLogger(this.getClass());
    private JwtTokenProvider jwtTokenProvider;

    public CookieAuthenticationFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
        setAuthenticationManager( super.getAuthenticationManager() );

    }
    public void setJwtTokenProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws RuntimeException {
        log.info("CookieAuthenticationFilter.attemptAuthentication ::::");
        if(request.getRequestURI().matches(".*/api/member.*"))
            return new JwtAuthenticationToken("ismember", null, null);
        String token = Optional.of(jwtTokenProvider.resolveToken(request))
                .orElseGet(()->jwtTokenProvider.resolveKakaoCookie(request));
        return new JwtAuthenticationToken(token,null,null);
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException,RuntimeException {
        log.info("CookieAuthenticationFilter.doFilter ::::");
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        try{
           JwtAuthenticationToken authentication = (JwtAuthenticationToken) this.attemptAuthentication(request,response);
           if(authentication.getToken() != null)
            SecurityContextHolder.getContext().setAuthentication(authentication);
           else
               throw new AuthenticationCredentialsNotFoundException("error");
            chain.doFilter(req,res);
        }catch(AuthenticationException e){
            log.error(e.toString());
            log.error("dofilter error");
            unsuccessfulAuthentication(request,response,e);
        }
    }

}
