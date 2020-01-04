package com.webapp.timeline.membership.web;

import com.webapp.timeline.membership.service.ValidateService;
import com.webapp.timeline.membership.service.response.ValidationInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Api(tags = {"1. Validate"})
@RequestMapping(value = "/api/member")
@CrossOrigin(origins = {"*"})
@RestController
public class ValidateController {
    private ValidateService validateService;

    public ValidateController(){
    }
    @Autowired
    public ValidateController(ValidateService validateService){
        this.validateService = validateService;
    }

    @ApiOperation(value = "회원가입 시 아이디 확인", notes = "존재하는 아이디인지 알려줌")
    @GetMapping(value="/id")
    public ValidationInfo validateIdExists(@RequestParam String id, HttpServletResponse response){
         return validateService.checkId(id,response);
    }
    @ApiOperation(value = "회원가입 시 이메일 확인", notes = "존재하는 이메일인지 알려줌")
    @GetMapping(value="/email")
    public ValidationInfo validateEmailExists(@RequestParam String email,@RequestParam String id,HttpServletResponse response){
        return validateService.checkEmail(email,id,response);
    }
    @ApiOperation(value = "회원가입 시 핸드폰 번호 확인", notes = "존재하는 핸드폰 번호인지 알려줌")
    @GetMapping(value="/phone")
    public ValidationInfo validatePhoneExists (@RequestParam String phone,@RequestParam String id,HttpServletResponse response){
        return validateService.checkPhone(phone,id,response);
    }
}
