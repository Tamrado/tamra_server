package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.CustomPasswordEncoder;
import com.webapp.timeline.membership.security.JwtTokenProvider;
import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.result.SingleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public SingleResult<String> validateUser(Users users){
        SingleResult<String> singleResult = signUpValidator.validate(users);
        if(!singleResult.getSuccess()) return singleResult;
        return initUserforSignUp(users);
    }
    public SingleResult<String> userImageUpload(MultipartFile multipartFile, String userId) {
        SingleResult<String> singleResult = new SingleResult<>();
        try {
            if(loadUserByUsername(userId) == null) {
                singleResult.setMsg("no user");
                return singleResult;
            }
            else singleResult = userImageS3Component.upload(multipartFile, userId);
        }
        catch(IOException e){
            log.error(e.toString());
        }
        return userService.saveImageURL(singleResult,userId);
    }
    public SingleResult<String> initUserforSignUp(Users user){
        user.setPassword(customPasswordEncoder.encode(user.getPassword()));
        user.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
        user.setAuthorities();
        return saveUser(user);
    }
    public SingleResult <String> findUser(String id, String password) {
        Users foundedUser = loadUserByUsername(id);
        SingleResult<String> singleResult = new SingleResult<String>();
        if(foundedUser == null) singleResult.setMsg("wrong user");
        else if(customPasswordEncoder.matches(password, foundedUser.getPassword()))
            singleResult.setSuccessResult(200,"same user");
        else
            singleResult.setMsg("wrong password");

        return putToken(singleResult,foundedUser.getId());
    }

    private SingleResult<String> putToken(SingleResult<String> singleResult,String id){
        if(id != null && singleResult.getSuccess()) {
            jwtTokenProvider = new JwtTokenProvider(new UserSignService());
            singleResult.setData(jwtTokenProvider.createToken(id));
        }
        else
            singleResult.setFailResult(404, "fail");
        return singleResult;
    }
    public SingleResult<String> saveUser(Users user) {
        SingleResult<String> singleResult = new SingleResult<String>();
        try {
            usersEntityRepository.save(user);
            usersEntityRepository.flush();
            singleResult.setSuccessResult(200,"success save");
        } catch (Exception e) {
            singleResult.setMsg("fail to save");
        }
        return putToken(singleResult,user.getId());
    }

}
