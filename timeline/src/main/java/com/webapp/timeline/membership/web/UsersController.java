package com.webapp.timeline.membership.web;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.UserModifyService;
import com.webapp.timeline.membership.service.UserService;
import com.webapp.timeline.membership.service.UserSignService;
import com.webapp.timeline.membership.service.result.CommonResult;
import com.webapp.timeline.membership.service.result.LoggedInfo;
import com.webapp.timeline.membership.service.result.SingleResult;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(tags = {"1. User"})
@RequestMapping(value = "/api")
@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class UsersController {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private UserModifyService userModifyService;
    private UserSignService userSignService;
    private UserService userService;
    private TokenService tokenService;
    @Autowired
    public UsersController(TokenService tokenService,UserSignService userSignService,UserModifyService userModifyService,UserService userService){
        this.userModifyService = userModifyService;
        this.userSignService = userSignService;
        this.userService = userService;
        this.tokenService = tokenService;
    }
    public UsersController(){

    }
    @ApiOperation(value = "회원 가입" , notes = "회원을 추가함")
    @PostMapping(value="/member")
    public SingleResult<String> signUp(@ApiParam(value = "회원정보") @RequestBody Users users,HttpServletResponse response){
        log.debug("signUp");
        SingleResult<String> result = userSignService.validateUser(users);
        if(result.getSuccess())
           return tokenService.addCookie(response,result);
        else return result;
    }

    @ApiOperation(value = "회원 가입시 이미지 업로드", notes = "회원 이미지 업로드")
    @PostMapping(value = "/member/image", consumes = "multipart/form-data")
    public LoggedInfo imageUpload(@RequestParam(value ="file",required=false) MultipartFile file, @RequestParam(value = "userId") String userId)throws IOException {
        CommonResult commonResult = userSignService.userImageUpload(file,userId);
        return userService.setLoggedInfo(new SingleResult<String>(commonResult.getSuccess(),
                commonResult.getCode(),commonResult.getMsg()),userId);
    }

    @ApiOperation(value = "로그인", notes = "회원인지 아닌지 확인 후 token 발급")
    @PostMapping(value="/member/auth")
    public LoggedInfo signIn(@RequestBody Map<String,Object> user, HttpServletResponse response){
        SingleResult <String> singleResult = userSignService.findUser((String)user.get("id"),(String)user.get("password"));

        if(singleResult.getData() != null)
            tokenService.addCookie(response,singleResult);
        else
            response.setStatus(404);
        return userService.setLoggedInfo(singleResult,(String)user.get("id"));
    }

    @ApiOperation(value="로그아웃", notes = "로그아웃으로 토큰 삭제")
    @GetMapping(value="/member/auth/token")
    public CommonResult signOut(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        return tokenService.removeCookie(httpServletRequest,httpServletResponse);
    }

    @ApiOperation(value="accesstoken 확인", notes="accesstoken 확인 후 있으면 갱신")
    @GetMapping(value="/member/auth/token/id")
    public CommonResult checkAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return tokenService.checkCookieAndRenew(httpServletRequest,httpServletResponse);
    }
    @ApiOperation(value="개인정보 수정",notes = "회원의 개인정보를 수정함")
    @PutMapping(value="/member")
    public CommonResult modify(@ApiParam(value= "수정하고자 하는 값") @RequestBody Users user){
        log.error("modify");

        return userModifyService.modifyUser(user);
    }

    @ApiOperation(value="개인정보 수정(사진)",notes = "회원의 개인정보를 수정함(사진)")
    @PostMapping(value="/member/image/id")
    public CommonResult modifyImage(@RequestParam(value ="file",required=false) MultipartFile file) throws IOException{
        return userModifyService.modifyImage(file);
    }

    @ApiOperation(value="유저 확인", notes = "올바른 유저인지 확인")
    @PostMapping(value="/auth")
    public CommonResult correctUser(@ApiParam(value = "비밀번호") @RequestBody Map<String,String> user){
        return userService.confirmCorrectUser(user.get("password"));
    }

    @ApiOperation(value="비활성화",notes = "유저가 비활성화 함")
    @PutMapping(value="/member/id")
    public SingleResult changetoInactive(){
        return userModifyService.modifyIdentify();
    }

}
