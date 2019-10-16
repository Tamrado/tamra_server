package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.CustomPasswordEncoder;
import com.webapp.timeline.membership.security.JwtTokenProvider;
import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.result.CommonResult;
import com.webapp.timeline.membership.service.result.SingleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Configurable
@Service
public class UserSignService implements UserDetailsService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private UsersEntityRepository usersEntityRepository;
    private CustomPasswordEncoder customPasswordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;
    private SignUpValidator signUpValidator;
    private UserImageS3Component userImageS3Component;

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
    public CommonResult validateUser(Users users){
        CommonResult commonResult = signUpValidator.validate(users);
        if(!commonResult.getSuccess()) return commonResult;
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
    public CommonResult initUserforSignUp(Users user){
        user.setPassword(customPasswordEncoder.encode(user.getPassword()));
        user.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
        user.setAuthorities();
        return userService.saveUser(user);
    }
    public SingleResult <String> findUser(String id, String password) {
        Users foundedUser = loadUserByUsername(id);
        SingleResult<String> singleResult = new SingleResult<String>();
        if(foundedUser == null) singleResult.setMsg("wrong user");
        else if(customPasswordEncoder.matches(password, foundedUser.getPassword())) {
            jwtTokenProvider = new JwtTokenProvider(new UserSignService());
            String jwt = jwtTokenProvider.createToken(foundedUser.getId());
            singleResult.setSuccessResult(200,"same user",jwt);
        }
        else
            singleResult.setMsg("wrong password");

        return singleResult;
    }

}
