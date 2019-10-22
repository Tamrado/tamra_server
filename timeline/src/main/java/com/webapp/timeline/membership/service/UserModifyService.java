package com.webapp.timeline.membership.service;


import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.CustomPasswordEncoder;
import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.result.ValidationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@Transactional
@Service
public class UserModifyService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private UsersEntityRepository usersEntityRepository;
    private CustomPasswordEncoder customPasswordEncoder;
    private SignUpValidator signUpValidator;
    private UserImageS3Component userImageS3Component;
    private UserService userService;
    private UserSignService userSignService;
    private TokenService tokenService;
    @Autowired
    public UserModifyService(UserSignService userSignService,UserService userService,UserImageS3Component userImageS3Component,SignUpValidator signUpValidator,CustomPasswordEncoder customPasswordEncoder, UsersEntityRepository usersEntityRepository,TokenService tokenService){
        this.signUpValidator = signUpValidator;
        this.userSignService = userSignService;
        this.userImageS3Component = userImageS3Component;
        this.customPasswordEncoder = customPasswordEncoder;
        this.usersEntityRepository = usersEntityRepository;
        this.tokenService = tokenService;
        this.userService = userService;
    }
    public ValidationInfo modifyUser(Users user, HttpServletResponse response){
        ValidationInfo validationInfo = signUpValidator.validateForModify(user,response);
        if(validationInfo.getIssue() == null) {
            if(userSignService.loadUserByUsername(user.getId()) != null) {
                user.setPassword(customPasswordEncoder.encode(user.getPassword()));
                usersEntityRepository.updateUser( user.getGender(), user.getComment(), user.getAddress(), user.getUsername(), user.getEmail(), user.getPassword(), user.getPhone(), user.getId());
            }
            else{
                response.setStatus(404);
                validationInfo.setIssue("noUser");
                validationInfo.setObjectName("user");
            }
        }
        return validationInfo;
    }
    public void modifyImage(MultipartFile file, HttpServletResponse response){
        Users user = userService.extractUserFromToken();
        if(user == null) {
            response.setStatus(404);
            return;
        }
        else {
            try {
                userImageS3Component.upload(file, user.getId(),response);
            } catch (IOException e) {
                response.setStatus(404);
            }
        }
    }
    //로그아웃도 시켜야 한다.
    public void modifyIdentify(HttpServletRequest request, HttpServletResponse response){
        Users user = userService.extractUserFromToken();
        if(user == null) {
            response.setStatus(404);
        }
        else{
            user.setAuthoritytoInactive();
            tokenService.removeCookie(request,response);
            response.setStatus(200);
        }
    }


}
