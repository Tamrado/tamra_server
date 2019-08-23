package com.webapp.timeline.web;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.service.membership.CommonResult;
import com.webapp.timeline.service.membership.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1. User"})
@RequestMapping(value = "/user")
@RestController
public class UsersController {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private UserServiceImpl userServiceImpl;

    @Autowired
    public UsersController(UserServiceImpl userServiceImpl){
        this.userServiceImpl = userServiceImpl;
    }
    public UsersController(){

    }
    @ApiOperation(value = "회원 가입" , notes = "회원을 추가함")
    @PostMapping(value="/signUp")
    public CommonResult signUp(@ApiParam(value = "회원정보") @RequestBody UsersRequest usersRequest){
        Users users = new Users(usersRequest.getId(),usersRequest.getPassword(),usersRequest.getUsername(),usersRequest.getPhone(),usersRequest.getEmail(),usersRequest.getBirthday(),usersRequest.getGender(),usersRequest.getAddress(),usersRequest.getComment(),usersRequest.getProfileUrl(),new java.sql.Date(System.currentTimeMillis()),usersRequest.getGroup1(),usersRequest.getGroup2(),usersRequest.getGroup3(),usersRequest.getGroup4());
        CommonResult commonResult = userServiceImpl.validateUser(users);
        return commonResult;
    }




}
