package com.webapp.timeline.web;

import com.webapp.timeline.domain.Users;
<<<<<<< HEAD
import com.webapp.timeline.service.result.CommonResult;
=======
import com.webapp.timeline.service.membership.CommonResult;
>>>>>>> 1bb85d954bff476da70b1b312038057e1a9640aa
import com.webapp.timeline.service.membership.UserServiceImpl;
import com.webapp.timeline.service.result.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD

import java.util.Map;
=======
>>>>>>> 1bb85d954bff476da70b1b312038057e1a9640aa

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
<<<<<<< HEAD
    public CommonResult signUp(@ApiParam(value = "회원정보") @RequestBody Users users){
=======
    public CommonResult signUp(@ApiParam(value = "회원정보") @RequestBody UsersRequest usersRequest){
        Users users = new Users(usersRequest.getId(),usersRequest.getPassword(),usersRequest.getUsername(),usersRequest.getPhone(),usersRequest.getEmail(),usersRequest.getBirthday(),usersRequest.getGender(),usersRequest.getAddress(),usersRequest.getComment(),usersRequest.getProfileUrl(),new java.sql.Date(System.currentTimeMillis()),usersRequest.getGroup1(),usersRequest.getGroup2(),usersRequest.getGroup3(),usersRequest.getGroup4());
>>>>>>> 1bb85d954bff476da70b1b312038057e1a9640aa
        CommonResult commonResult = userServiceImpl.validateUser(users);
        return commonResult;
    }
    @ApiOperation(value = "로그인", notes = "회원인지 아닌지 확인 후 accessToken 발급")
    @PostMapping(value="/signIn")
    public SingleResult<String> signIn(@RequestBody Map<String,Object> user){
        SingleResult<String> singleResult = userServiceImpl.findUser((String)user.get("id"),(String)user.get("password"));
        return singleResult;
    }




}
