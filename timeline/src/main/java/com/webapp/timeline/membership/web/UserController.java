package com.webapp.timeline.membership.web;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.UserModifyService;
import com.webapp.timeline.membership.service.UserService;
import com.webapp.timeline.membership.service.result.LoggedInfo;
import com.webapp.timeline.membership.service.result.ValidationInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(tags = {"2. User"})
@RequestMapping(value = "/api")
@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class UserController {

    Logger log = LoggerFactory.getLogger(this.getClass());
    private TokenService tokenService;
    private UserModifyService userModifyService;
    private UserService userService;

    @Autowired
    public UserController(UserService userService, UserModifyService userModifyService, TokenService tokenService){
        this.userModifyService = userModifyService;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    public UserController(){}

    @ApiOperation(value = "유저인지 확인 후 유저 정보 전달",notes = "쿠키 내 accesstoken에서 userid 로 확인 후 유저 정보 전달")
    @GetMapping(value="/member/info")
    public LoggedInfo checkUserAndSendProfile(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        log.error("info");
        return tokenService.sendInfo(httpServletRequest,httpServletResponse);
    }

    @ApiOperation(value="accesstoken 확인", notes="accesstoken 확인 후 있으면 갱신")
    @GetMapping(value="/member/auth/token/id")
    public void checkAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        tokenService.checkCookieAndRenew(httpServletRequest,httpServletResponse);
    }
    @ApiOperation(value="개인정보 수정",notes = "회원의 개인정보를 수정함")
    @PutMapping(value="/member")
    public ValidationInfo modify(@ApiParam(value= "수정하고자 하는 값") @RequestBody Users user, HttpServletResponse httpServletResponse){
        log.error("modify");
        return userModifyService.modifyUser(user,httpServletResponse);
    }

    @ApiOperation(value="개인정보 수정(사진)",notes = "회원의 개인정보를 수정함(사진)")
    @PostMapping(value="/member/image/id")
    public void modifyImage(@RequestParam(value ="file",required=false) MultipartFile file,HttpServletResponse httpServletResponse) throws IOException {
        userModifyService.modifyImage(file,httpServletResponse);
    }

    @ApiOperation(value="유저 확인", notes = "비밀번호 확인으로 올바른 유저인지 확인")
    @PostMapping(value="/auth")
    public void correctUserPassword(@ApiParam(value = "비밀번호") @RequestBody Map<String,String> user,HttpServletResponse httpServletResponse){
        userService.confirmCorrectUser(user.get("password"),httpServletResponse);
    }

    @ApiOperation(value="비활성화",notes = "유저가 비활성화 함")
    @PutMapping(value="/member/id")
    public void changetoInactive(HttpServletRequest request,HttpServletResponse response){
        userModifyService.modifyIdentify(request,response);
    }

}
