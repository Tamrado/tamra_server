package com.webapp.timeline.service.membership;
import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import com.webapp.timeline.security.CustomPasswordEncoder;
import com.webapp.timeline.security.SignUpValidator;
import com.webapp.timeline.service.result.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    public UserModifyService(UserService userService,UserImageS3Component userImageS3Component,SignUpValidator signUpValidator,CustomPasswordEncoder customPasswordEncoder, UsersEntityRepository usersEntityRepository){
        this.signUpValidator = signUpValidator;
        this.userImageS3Component = userImageS3Component;
        this.customPasswordEncoder = customPasswordEncoder;
        this.usersEntityRepository = usersEntityRepository;
        this.userService = userService;
    }
    public CommonResult modifyUser(Users user){
        CommonResult commonResult = signUpValidator.validateForModify(user);
        if(commonResult.getSuccess()) {
            user.setPassword(customPasswordEncoder.encode(user.getPassword()));
            usersEntityRepository.updateUser(user.getGroup4(), user.getGroup3(), user.getGroup2(), user.getGroup1(), user.getGender(),user.getComment(), user.getAddress(), user.getUsername(), user.getEmail(), user.getPassword(), user.getPhone(), user.getId());
            commonResult.setMsg("update user");
        }
        return commonResult;
    }
    public CommonResult modifyImage(MultipartFile file){
        CommonResult commonResult = new CommonResult();
        Users user = userService.extractUserFromToken();
        try {
            return userImageS3Component.upload(file, user.getId());
        }
        catch(IOException e){
            commonResult.setMsg(e.toString());
            log.error(e.toString());
        }
        return commonResult;
    }


}
