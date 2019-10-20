package com.webapp.timeline.membership.web;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.UserModifyService;
import com.webapp.timeline.membership.service.UserService;
import com.webapp.timeline.membership.service.UserSignService;
import com.webapp.timeline.membership.service.result.CommonResult;
import com.webapp.timeline.membership.service.result.SingleResult;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
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
    @Autowired
    public UsersController(UserSignService userSignService,UserModifyService userModifyService,UserService userService){
        this.userModifyService = userModifyService;
        this.userSignService = userSignService;
        this.userService = userService;
    }
    public UsersController(){

    }
    @ApiOperation(value = "회원 가입" , notes = "회원을 추가함")
    @PostMapping(value="/member")
    public CommonResult signUp(@ApiParam(value = "회원정보") @RequestBody Users users){
        log.debug("signUp");
        return userSignService.validateUser(users);
    }

    @ApiOperation(value = "회원 가입시 이미지 업로드", notes = "회원 이미지 업로드")
    @PostMapping(value = "/member/image", consumes = "multipart/form-data")
    public CommonResult imageUpload(@RequestParam(value ="file",required=false) MultipartFile file, @RequestParam(value = "userId") String userId)throws IOException {
        log.info("image upload");
        return userSignService.userImageUpload(file,userId);
    }

    @ApiOperation(value = "로그인", notes = "회원인지 아닌지 확인 후 token 발급")
    @PostMapping(value="/member/auth")
    public SingleResult<String> signIn(@RequestBody Map<String,Object> user, HttpServletResponse response){
        SingleResult <String> singleResult = userSignService.findUser((String)user.get("id"),(String)user.get("password"));
        if(singleResult.getData() != null) {
            Cookie cookie = new Cookie("accesstoken", singleResult.getData());
            cookie.setMaxAge(1000 * 60 * 60);
            response.addCookie(cookie);
            response.setStatus(200);
        }
        return singleResult;
    }

    @ApiOperation(value="개인정보 수정",notes = "회원의 개인정보를 수정함")
    @PutMapping(value="/member")
    public CommonResult modify(@ApiParam(value= "수정하고자 하는 값") @RequestBody Users user,@RequestHeader("X-AUTH-TOKEN")String token){
        log.error("modify");
        return userModifyService.modifyUser(user);
    }

    @ApiOperation(value="개인정보 수정(사진)",notes = "회원의 개인정보를 수정함(사진)")
    @PostMapping(value="/member/image/id")
    public CommonResult modifyImage(@RequestParam(value ="file",required=false) MultipartFile file,@RequestHeader("X-AUTH-TOKEN")String token) throws IOException{
        return userModifyService.modifyImage(file);
    }

    @ApiOperation(value="유저 확인", notes = "올바른 유저인지 확인")
    @PostMapping(value="/auth")
    public CommonResult correctUser(@ApiParam(value = "비밀번호") @RequestBody Map<String,String> user,@RequestHeader("X-AUTH-TOKEN")String token){
        return userService.confirmCorrectUser(user.get("password"));
    }

    @ApiOperation(value="비활성화",notes = "유저가 비활성화 함")
    @PutMapping(value="/member/id")
    public SingleResult changetoInactive(@RequestHeader("X-AUTH-TOKEN")String token){
        return userModifyService.modifyIdentify();
    }

}
