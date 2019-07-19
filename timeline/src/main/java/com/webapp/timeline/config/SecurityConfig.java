package com.webapp.timeline.config;

import com.webapp.timeline.security.CustomPasswordEncoder;
import com.webapp.timeline.service.membership.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private UserServiceImpl userServiceImpl;

    @Autowired
    private void setUserServiceImpl(UserServiceImpl userServiceImpl){
        this.userServiceImpl = userServiceImpl;
    }

    @Bean
    public PasswordEncoder passwordEncoder() throws IllegalArgumentException{

        return new CustomPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception{

    }
    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http.formLogin().loginProcessingUrl("/login");
        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/");
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.eraseCredentials(true).
                userDetailsService(userServiceImpl).
                passwordEncoder(passwordEncoder());

    }



}
