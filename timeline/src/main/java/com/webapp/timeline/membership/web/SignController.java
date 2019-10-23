package com.webapp.timeline.membership.web;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.UserModifyService;
import com.webapp.timeline.membership.service.UserService;
import com.webapp.timeline.membership.service.UserSignService;
import com.webapp.timeline.membership.service.result.LoggedInfo;
import com.webapp.timeline.membership.service.result.ValidationInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(tags = {"1. Sign"})
@RequestMapping(value = "/api")
@RestController
public class SignController {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private UserModifyService userModifyService;
    private UserSignService userSignService;
    private UserService userService;
    private TokenService tokenService;
    @Autowired
    public SignController(TokenService tokenService, UserSignService userSignService, UserModifyService userModifyService, UserService userService){
        this.userModifyService = userModifyService;
        this.userSignService = userSignService;
        this.userService = userService;
        this.tokenService = tokenService;
    }
    public SignController(){

    }
    @ApiOperation(value = "회원 가입" , notes = "회원을 추가함")
    @PostMapping(value="/member")
    public ValidationInfo signUp(@ApiParam(value = "회원정보") @RequestBody Users users, HttpServletResponse response){
        response.setStatus(200);
        return userSignService.validateUser(users,response);

    }

    @ApiOperation(value = "회원 가입시 이미지 업로드", notes = "회원 이미지 업로드")
    @PostMapping(value = "/member/image", consumes = "multipart/form-data")
    public LoggedInfo imageUpload(@RequestParam(value ="file",required=false) MultipartFile file, @RequestParam(value = "userId") String userId,HttpServletResponse httpServletResponse)throws IOException {
        httpServletResponse.setStatus(200);
        userSignService.userImageUpload(file,userId,httpServletResponse);
        return tokenService.addCookie(httpServletResponse,userId);
    }

    @ApiOperation(value = "로그인", notes = "회원인지 아닌지 확인 후 token 발급")
    @PostMapping(value="/member/auth")
    public LoggedInfo signIn(@RequestBody Map<String,Object> user, HttpServletResponse response){
        response.setStatus(200);
        return tokenService.findUserAndAddCookie(response,user);
    }

    @ApiOperation(value="로그아웃", notes = "로그아웃으로 토큰 삭제")
    @GetMapping(value="/member/auth/token")
    public void signOut(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        tokenService.removeCookie(httpServletRequest,httpServletResponse);
    }


}
