package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.service.response.LoggedInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@Service
public class UserSignImageServiceImpl implements UserSignImageService {
    private UserSignServiceImpl userSignServiceImpl;
    private TokenService tokenService;
    private UserService userService;
    private UserImageS3Component userImageS3Component;

    public UserSignImageServiceImpl(){}
    @Autowired
    public UserSignImageServiceImpl(UserImageS3Component userImageS3Component,UserService userService,TokenService tokenService,UserSignServiceImpl userSignServiceImpl){
        this.tokenService = tokenService;
        this.userSignServiceImpl = userSignServiceImpl;
        this.userService = userService;
        this.userImageS3Component = userImageS3Component;
    }
    @Override
    public LoggedInfo uploadUserSignImage(MultipartFile file, String userId, HttpServletResponse response) throws RuntimeException, IOException {
        userImageUpload(file,userId);
        return tokenService.addCookie(response,userId);
    }
    @Transactional
    @Override
    public void userImageUpload(MultipartFile multipartFile, String userId) throws RuntimeException,IOException{
        userSignServiceImpl.loadUserByUsername(userId);
        userService.saveImageURL(userId,userImageS3Component.upload(multipartFile, userId));
    }
}
