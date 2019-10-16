package com.webapp.timeline.membership.service;


import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.CustomPasswordEncoder;
import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.result.CommonResult;
import com.webapp.timeline.membership.service.result.SingleResult;
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
    private UserSignService userSignService;
    @Autowired
    public UserModifyService(UserSignService userSignService,UserService userService,UserImageS3Component userImageS3Component,SignUpValidator signUpValidator,CustomPasswordEncoder customPasswordEncoder, UsersEntityRepository usersEntityRepository){
        this.signUpValidator = signUpValidator;
        this.userSignService = userSignService;
        this.userImageS3Component = userImageS3Component;
        this.customPasswordEncoder = customPasswordEncoder;
        this.usersEntityRepository = usersEntityRepository;
        this.userService = userService;
    }
    public CommonResult modifyUser(Users user){
        CommonResult commonResult = signUpValidator.validateForModify(user);
        if(commonResult.getSuccess()) {
            if(userSignService.loadUserByUsername(user.getId()) != null) {
                user.setPassword(customPasswordEncoder.encode(user.getPassword()));
                usersEntityRepository.updateUser(user.getGroup4(), user.getGroup3(), user.getGroup2(), user.getGroup1(), user.getGender(), user.getComment(), user.getAddress(), user.getUsername(), user.getEmail(), user.getPassword(), user.getPhone(), user.getId());
                commonResult.setMsg("update user");
            }
            else{
                commonResult.setMsg("no user");
                commonResult.setCode(405);
                commonResult.setSuccess(false);
            }
        }
        return commonResult;
    }
    public CommonResult modifyImage(MultipartFile file){
        CommonResult commonResult = new CommonResult();
        Users user = userService.extractUserFromToken();
        if(user == null)
            commonResult.setFailResult(405,"no user");
        else {
            try {
                return userImageS3Component.upload(file, user.getId());
            } catch (IOException e) {
                commonResult.setMsg(e.toString());
                log.error(e.toString());
            }
        }
        return commonResult;
    }
    //로그아웃도 시켜야 한다.
    public SingleResult modifyIdentify(){
        SingleResult singleResult = new SingleResult();
        Users user = userService.extractUserFromToken();
        if(user == null)
            singleResult.setFailResult(405,"user no login");
        else{
            user.setAuthoritytoInactive();
            singleResult.setSuccessResult(200,"Succeed in setting the deactivation");
        }
        return singleResult;
    }


}
