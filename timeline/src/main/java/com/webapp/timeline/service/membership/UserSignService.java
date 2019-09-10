package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Profiles;
import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import com.webapp.timeline.security.CustomPasswordEncoder;
import com.webapp.timeline.security.JwtTokenProvider;
import com.webapp.timeline.security.SignUpValidator;
import com.webapp.timeline.service.result.CommonResult;
import com.webapp.timeline.service.result.SingleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public Users loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersEntityRepository.findIdByExistingId(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        return user;
    }
    public SingleResult<Long> validateUser(Users users){
        CommonResult commonResult = new SingleResult<>();
        commonResult = signUpValidator.validate(users);
        if(!commonResult.getSuccess()) return (SingleResult<Long>) commonResult;
        return initUserforSignUp(users);
    }
    public SingleResult<String> userImageUpload(MultipartFile multipartFile, String userId) {
        SingleResult<String> singleResult = new SingleResult<>();
        try {
            singleResult = userImageS3Component.upload(multipartFile, userId);
        }
        catch(IOException e){
            log.error(e.toString());
        }
        return userService.saveImageURL(singleResult,userId);
    }
    public SingleResult<Long> initUserforSignUp(Users user){
        user.setPassword(customPasswordEncoder.encode(user.getPassword()));
        user.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
        user.setAuthorities();
        return userService.saveUser(user);
    }
    public SingleResult <String> findUser(String id, String password) {
        Users foundedUser = loadUserByUsername(id);
        SingleResult<String> singleResult = new SingleResult<String>();
        if(customPasswordEncoder.matches(password, foundedUser.getPassword())) {
            jwtTokenProvider = new JwtTokenProvider(new UserSignService());
            String jwt = jwtTokenProvider.createToken(foundedUser.getId());
            singleResult.setCode(200);
            singleResult.setSuccess(true);
            singleResult.setMsg("same user");
            singleResult.setData(jwt);
        }
        else
            singleResult.setMsg("wrong user");

        return singleResult;
    }

}
