package com.webapp.timeline.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/*
AuthenticationEntryPoint : 인증된 사용자가 SecurityContext에 존재하지도 않고,
 어떠한 인증되지 않은 익명의 사용자가 보호된 리소스에 접근하였을 때, 수행되는 EntryPoint 핸들러이다.

 */
public class CustomAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    Logger log = LoggerFactory.getLogger(this.getClass());
    public CustomAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.debug("CustomAuthenticationEntryPoint.commence ::::");
        log.error("entrypoint");
        response.sendRedirect("/exception/entrypoint");
    }

}
