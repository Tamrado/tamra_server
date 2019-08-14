package com.webapp.timeline.web;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import com.webapp.timeline.service.membership.CommonResult;
import com.webapp.timeline.service.membership.ResponseService;
import com.webapp.timeline.service.membership.SingleResult;
import com.webapp.timeline.service.membership.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CommonAbstractCriteria;
import java.util.Date;

@Api(tags = {"1. User"})
@RequestMapping(value = "/user")
@RestController
public class UsersController {

    private UsersEntityRepository usersEntityRepository;
    private UserServiceImpl userServiceImpl;
    private ResponseService responseService;
    @Autowired
    private UsersController(UsersEntityRepository usersEntityRepository, UserServiceImpl userServiceImpl, ResponseService responseService){
        this.usersEntityRepository = usersEntityRepository;
        this.userServiceImpl = userServiceImpl;
        this.responseService = responseService;
    }

    @ApiOperation(value = "회원 가입" , notes = "회원을 추가함")
    @PostMapping(value="/signUp")
    public CommonResult signUp(@ApiParam(value = "회원ID")@RequestParam String id,
                             @ApiParam(value = "비밀번호")@RequestParam String password,
                             @ApiParam(value = "이름")@RequestParam String name,
                             @ApiParam(value="핸드폰 번호") @RequestParam String phone,
                             @ApiParam(value="이메일")@RequestParam String email,
                             @ApiParam(value="생년월일", required = false)@RequestParam Date birthday,
                             @ApiParam(value="성별")@RequestParam int gender,
                             @ApiParam(value="주소",required = false)@RequestParam String address,
                             @ApiParam(value="코멘트",required = false)@RequestParam String comment,
                             @ApiParam(value="프로필사진", required = false)@RequestParam String profileUrl,
                             @ApiParam(value="계정 생성 날짜")@RequestParam Date timestamp,
                             @ApiParam(value="그룹 1", required = false)@RequestParam String group1,
                             @ApiParam(value="그룹 2", required = false)@RequestParam String group2,
                             @ApiParam(value="그룹 3", required = false)@RequestParam String group3,
                             @ApiParam(value="그룹 4", required = false)@RequestParam String group4){


        return responseService.getSuccessResult();
    }




}
