package com.webapp.timeline.membership.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        if (!(authentication instanceof JwtAuthenticationToken)) {
            return;
        }

        JwtAuthenticationToken jwtAuthenticaton = (JwtAuthenticationToken) authentication;

        // Add a session cookie
        Cookie accesstoken = new Cookie("accesstoken", jwtAuthenticaton.getToken());
        response.addCookie(accesstoken);

        // call the original impl
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
