package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.CustomPasswordEncoder;
import com.webapp.timeline.membership.security.JwtTokenProvider;
import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.result.LoggedInfo;
import com.webapp.timeline.membership.service.result.ValidationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Transactional
@Configurable
@Service
public class UserSignService implements UserDetailsService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private UsersEntityRepository usersEntityRepository;
    private CustomPasswordEncoder customPasswordEncoder;
    private UserService userService;
    private SignUpValidator signUpValidator;
    private UserImageS3Component userImageS3Component;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserSignService(UserImageS3Component userImageS3Component,SignUpValidator signUpValidator,UsersEntityRepository usersEntityRepository,CustomPasswordEncoder customPasswordEncoder, UserService userService) {
        this.userImageS3Component = userImageS3Component;
        this.signUpValidator = signUpValidator;
        this.usersEntityRepository = usersEntityRepository;
        this.customPasswordEncoder = customPasswordEncoder;
        this.userService = userService;
    }
    public UserSignService(){
    }
    @Override
    public Users loadUserByUsername(String username) {
        Users user = usersEntityRepository.findIdByExistingId(username);
        log.info("loadUserByUsername");
        return user;
    }
    public ValidationInfo validateUser(Users users, HttpServletResponse response){
        ValidationInfo validationInfo = signUpValidator.validate(users,response);
        log.error(validationInfo.getIssue());
        if(validationInfo.getIssue() != null) return validationInfo;

        return initUserforSignUp(users,response);
    }
    public void userImageUpload(MultipartFile multipartFile, String userId, HttpServletResponse response) {
        try {
            response.setStatus(200);
            if(loadUserByUsername(userId) == null) {
                log.error(userId);
                response.setStatus(404);
            }
            else
            userService.saveImageURL(userId,response,userImageS3Component.upload(multipartFile, userId,response));
        }
        catch(IOException e){
            response.setStatus(400);
        }

    }
    public ValidationInfo initUserforSignUp(Users user,HttpServletResponse response){
        user.setPassword(customPasswordEncoder.encode(user.getPassword()));
        user.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
        user.setAuthorities();
        return saveUser(user,response);

    }
    public Boolean findUser(Map<String,Object> user, HttpServletResponse response) {
        Users foundedUser = loadUserByUsername(user.get("id").toString());
        if(foundedUser == null){
            response.setStatus(404);
            return false;
        }
        if(customPasswordEncoder.matches(user.get("password").toString(), foundedUser.getPassword()))
            return true;

        response.setStatus(400);
        return false;
    }

    private String putToken(String id,HttpServletResponse response){
        jwtTokenProvider = new JwtTokenProvider(new UserSignService());
        return jwtTokenProvider.createToken(id);
    }

    public ValidationInfo saveUser(Users user,HttpServletResponse response) {
        try {
            usersEntityRepository.save(user);
            usersEntityRepository.flush();
            response.setStatus(200);
        } catch (Exception e) {
            response.setStatus(400);
        }
        return null;
    }

}
