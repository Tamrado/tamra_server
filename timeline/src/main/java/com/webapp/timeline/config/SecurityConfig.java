package com.webapp.timeline.config;

import com.amazonaws.services.s3.AmazonS3Client;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.*;
import com.webapp.timeline.membership.service.UserImageS3Component;
import com.webapp.timeline.membership.service.UserServiceImpl;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.membership.service.interfaces.UserSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserSignServiceImpl userSignServiceImpl;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UsersEntityRepository usersEntityRepository;
    @Autowired
    private CustomPasswordEncoder customPasswordEncoder;

    @Override
    protected void configure( AuthenticationManagerBuilder auth ) throws Exception {
        jwtTokenProvider =  new JwtTokenProvider(userSignServiceImpl);
        userSignServiceImpl = new UserSignServiceImpl(new SignUpValidator(usersEntityRepository)
                ,usersEntityRepository
                ,customPasswordEncoder);
        auth.authenticationProvider( new CustomAuthenticationProvider(jwtTokenProvider,
                userSignServiceImpl) );
    }

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers("/swagger-resources/**","/webjars/**", "/swagger-ui.html","/swagger/**","/v2/api-docs"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/member/*").permitAll()
                .antMatchers("/api/auth/*").hasAuthority("ROLE_USER")
                .antMatchers("/api/friend/*").hasAuthority("ROLE_USER")
                .antMatchers("/api/list/*").hasAuthority("ROLE_USER")
                .antMatchers("/api/post/*").hasAuthority("ROLE_USER")
                .antMatchers("/api/newsfeed").hasAuthority("ROLE_USER")

                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .and()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .addFilterAfter( getCookieAuthenticationFilter(
                        new AndRequestMatcher( new AntPathRequestMatcher( "/api/*" ) )
                ) , UsernamePasswordAuthenticationFilter.class );
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
        successHandler.setDefaultTargetUrl("/index");
        return successHandler;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        CustomAuthenticationFailureHandler failureHandler = new CustomAuthenticationFailureHandler();
        failureHandler.setDefaultFailureUrl("/loginPage?error=error");
        return failureHandler;
    }
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        CustomLogoutSuccessHandler logoutSuccessHandler = new CustomLogoutSuccessHandler();
        logoutSuccessHandler.setDefaultTargetUrl("/loginPage?logout=logout");
        return logoutSuccessHandler;
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/error/403");
        return accessDeniedHandler;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint("/loginPage?error=e");
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    CookieAuthenticationFilter getCookieAuthenticationFilter(RequestMatcher requestMatcher ) {
        jwtTokenProvider =  new JwtTokenProvider(userSignServiceImpl);
        userSignServiceImpl = new UserSignServiceImpl(new SignUpValidator(usersEntityRepository)
                ,usersEntityRepository
                ,customPasswordEncoder);
        CookieAuthenticationFilter filter = new CookieAuthenticationFilter(requestMatcher);
        filter.setJwtTokenProvider(jwtTokenProvider);

        return filter;
    }

}
