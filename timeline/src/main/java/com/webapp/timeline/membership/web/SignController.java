package com.webapp.timeline.membership.web;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.*;
import com.webapp.timeline.membership.service.response.LoggedInfo;
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

@Api(tags = {"0. Sign"})
@RequestMapping(value = "/api")
@CrossOrigin(origins = {"*"})
@RestController
public class SignController {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private UserModifyServiceImpl userModifyService;
    private UserSignService userSignService;
    private UserService userService;
    private TokenService tokenService;
    private UserSignImageService userSignImageService;
    @Autowired
    public SignController(TokenService tokenService, UserSignImageService userSignImageService, UserSignService userSignService, UserModifyServiceImpl userModifyService, UserService userService){
        this.userModifyService = userModifyService;
        this.userSignService = userSignService;
        this.userService = userService;
        this.tokenService = tokenService;
        this.userSignImageService = userSignImageService;
    }
    public SignController(){

    }
    @ApiOperation(value = "회원 가입" , notes = "회원을 추가함 (response : 200 - 성공, 411 - 조건에 맞는 데이터가 아님, 409 - 겹치는 데이터가 있음 )")
    @PostMapping(value="/member")
    public void signUp(@ApiParam(value = "회원정보") @RequestBody Users users) throws RuntimeException{
        userSignService.validateUser(users);
    }

    @ApiOperation(value = "회원 가입시 이미지 업로드", notes = "회원 이미지 업로드 (response : 200 - 성공, 411 - 조건에 맞는 데이터가 아님, 409 - 겹치는 데이터가 있음, 422- aws 문제로 저장되지 않음)")
    @PostMapping(value = "/member/image", consumes = "multipart/form-data")
    public LoggedInfo imageUpload(@RequestParam(value ="file",required=false) MultipartFile file, @RequestParam(value = "userId")
            String userId,HttpServletResponse httpServletResponse)throws IOException,RuntimeException {
        return userSignImageService.uploadUserSignImage(file,userId,httpServletResponse);
    }

    @ApiOperation(value = "로그인", notes = "회원인지 아닌지 확인 후 token 발급 (response: 200 - 성공, 411 - 로그인 실패)")
    @PostMapping(value="/member/auth")
    public LoggedInfo signIn(@RequestBody Map<String,Object> user, HttpServletResponse response) throws RuntimeException{
        return tokenService.findUserAndAddCookie(user,response);
    }

    @ApiOperation(value="로그아웃", notes = "로그아웃으로 토큰 삭제")
    @GetMapping(value="/member/auth/token")
    public void signOut(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        tokenService.removeCookie(httpServletRequest,httpServletResponse);
    }


}
