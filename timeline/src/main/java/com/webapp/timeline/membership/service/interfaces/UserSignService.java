package com.webapp.timeline.membership.service.interfaces;

import com.webapp.timeline.membership.domain.Users;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface UserSignService {
    void saveUser(Users user) throws RuntimeException;
    void validateUser(Users users) throws RuntimeException;
    void initUserforSignUp(Users user) throws RuntimeException;
    void findUser(Map<String,Object> user) throws RuntimeException;
    Users extractUserFromToken(HttpServletRequest httpServletRequest) throws RuntimeException;
    void confirmCorrectUser(HttpServletRequest httpServletRequest ,String password) throws RuntimeException;
}
