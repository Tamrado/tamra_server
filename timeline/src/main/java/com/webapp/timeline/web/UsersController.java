package com.webapp.timeline.web;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.service.result.CommonResult;
import com.webapp.timeline.service.membership.UserServiceImpl;
import com.webapp.timeline.service.result.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


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
    public CommonResult signUp(@ApiParam(value = "회원정보") @RequestBody Users users){

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
