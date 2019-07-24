package com.webapp.timeline.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    Logger log = LoggerFactory.getLogger(this.getClass());
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.debug("JwtAuthorizationFilter.doFilterInternal ::::");
        /*
         * 쿠키 인증 토큰을 검사한다.
         * 만약 토큰 및 헤더에 대한 검사에 실패한다면,
         * AuthenticationEntryPoint에 위임하거나 혹은 HttpResponse에 적절한
         * 상태코드와 메시지를 담아서 리턴해준다.
         */
        super.doFilterInternal(request, response, chain);
    }

    /*
     * 성공시 처리 메소드
     */
    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              Authentication authResult) throws IOException {
        // TODO Auto-generated method stub
        super.onSuccessfulAuthentication(request, response, authResult);
    }

    /*
     * 실패시 처리 메소드
     */
    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException failed) throws IOException {
        // TODO Auto-generated method stub
        super.onUnsuccessfulAuthentication(request, response, failed);
    }

}
