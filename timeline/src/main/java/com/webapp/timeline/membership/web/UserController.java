package com.webapp.timeline.membership.web;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.UserModifyService;
import com.webapp.timeline.membership.service.UserModifyServiceImpl;
import com.webapp.timeline.membership.service.UserSignService;
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
@CrossOrigin(origins = {"*"})
@RestController
public class UserController {

    Logger log = LoggerFactory.getLogger(this.getClass());
    private TokenService tokenService;
    private UserModifyService userModifyService;
    private UserSignService userSignService;

    @Autowired
    public UserController(UserSignService userSignService, UserModifyService userModifyService, TokenService tokenService){
        this.userModifyService = userModifyService;
        this.tokenService = tokenService;
        this.userSignService = userSignService;
    }

    public UserController(){}

    @ApiOperation(value = "새로 고침 시 유저인지 확인 후 유저 정보 전달",notes = "쿠키 내 accesstoken에서 userid 로 확인 후 유저 정보 전달 (response : 200 - 성공, 411 - 유저 정보가 다름)")
    @GetMapping(value="/member/info")
    public LoggedInfo checkUserAndSendProfile(@RequestParam String id,HttpServletRequest httpServletRequest) throws RuntimeException{
        return tokenService.sendInfo(id,httpServletRequest);
    }
    @ApiOperation(value = "유저인지 확인 후 유저 수정 정보 전달",notes = "쿠키 내 accesstoken에서 userId 뽑고 유저 정보 전달 (response : 200 성공, 404 유저 정보 없음)")
    @GetMapping(value="/member/user")
    public Users checkUserAndSendUser(HttpServletRequest httpServletRequest) throws RuntimeException{
        return userSignService.extractUserFromToken(httpServletRequest);
    }
    @ApiOperation(value="accesstoken 확인", notes="accesstoken 확인 후 있으면 갱신 (response : 200 성공, 404 유저 없음)")
    @GetMapping(value="/member/auth/token/id")
    public void checkAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws RuntimeException{
        tokenService.checkCookieAndRenew(httpServletRequest,httpServletResponse);
    }
    @ApiOperation(value="개인정보 수정",notes = "회원의 개인정보를 수정함 (response : 200 - 성공 404 - 유저 아님 409 - 유저 id 존재하지 않음 )")
    @PutMapping(value="/member")
    public void modify(@ApiParam(value= "수정하고자 하는 값") @RequestBody Users user) throws RuntimeException{
        userModifyService.modifyUser(user);
    }

    @ApiOperation(value="개인정보 수정(사진)",notes = "회원의 개인정보를 수정함(사진) (response 200 - 성공 411 - 조건에 맞는 정보가 없음 404 - 유저가 아님 409 - aws 문제 422 - 유저 id 존재하지 않음)")
    @PostMapping(value="/member/image/id")
    public LoggedInfo modifyImage(@RequestParam(value ="file",required=false) MultipartFile file,HttpServletRequest httpServletRequest) throws IOException,RuntimeException {
        return userModifyService.modifyImage(httpServletRequest,file);
    }

    @ApiOperation(value="유저 확인", notes = "비밀번호 확인으로 올바른 유저인지 확인 (response : 200 - 성공 411 - 비밀번호 틀림 404- user 없음)")
    @PostMapping(value="/auth")
    public void checkCorrectUserAndPostInfo(@ApiParam(value = "비밀번호") @RequestBody Map<String,String> user,HttpServletRequest httpServletRequest)throws RuntimeException{
        userSignService.confirmCorrectUser(httpServletRequest,user.get("password"));
    }

    @ApiOperation(value="비활성화",notes = "유저가 비활성화 함 (response : 200 - 성공 411 - 맞는 정보가 없어서 비활성화 실패 404- user 없음)")
    @PutMapping(value="/member/id")
    public void changetoInactive(HttpServletRequest request,HttpServletResponse response) throws RuntimeException{
        userModifyService.modifyIdentify(request,response);
    }
    /*@ApiOperation(value="활성화",notes = "유저가 활성화 함 (response : 200 - 성공 411 - 맞는 정보가 없어서 활성화 실패)")
    @PutMapping(value="/member/uid")
    public void changetoActive(HttpServletRequest request,HttpServletResponse response) throws RuntimeException {
        userModifyService.modifyIdentify(request, response);
    }*/
}
