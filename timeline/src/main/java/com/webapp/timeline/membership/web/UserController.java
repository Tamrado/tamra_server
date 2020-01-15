package com.webapp.timeline.membership.web;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.interfaces.*;
import com.webapp.timeline.membership.service.response.LoggedInfo;
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

@Api(tags = {"1. User"})
@RequestMapping(value = "/api/auth")
@CrossOrigin(origins = {"*"})
@RestController
public class UserController {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private TokenService tokenService;
    private UserModifyService userModifyService;
    private UserSignService userSignService;
    private UserService userService;
    private AlarmService alarmService;
    private KakaoService kakaoService;

    @Autowired
    public UserController(KakaoService kakaoService,AlarmService alarmService,UserService userService,UserSignService userSignService, UserModifyService userModifyService, TokenService tokenService){
        this.userModifyService = userModifyService;
        this.tokenService = tokenService;
        this.userSignService = userSignService;
        this.userService = userService;
        this.alarmService = alarmService;
        this.kakaoService = kakaoService;
    }

    public UserController(){}

    @ApiOperation(value = "새로 고침 시 유저인지 확인 후 유저 정보 전달",notes = "쿠키 내 accesstoken에서 userid 로 확인 후 유저 정보 전달 (response : 200 - 성공, 411 - 유저 정보가 다름)")
    @GetMapping(value="/info")
    public LoggedInfo checkUserAndSendProfile(@RequestParam String id,HttpServletRequest httpServletRequest) throws RuntimeException{
        return tokenService.sendInfo(id,httpServletRequest);
    }
    @ApiOperation(value = "유저인지 확인 후 유저 수정 정보 전달",notes = "쿠키 내 accesstoken에서 userId 뽑고 유저 정보 전달 (response : 200 성공, 404 유저 정보 없음)")
    @GetMapping(value="/user")
    public Users checkUserAndSendUser(HttpServletRequest httpServletRequest) throws RuntimeException{
        return userSignService.extractUserFromToken(httpServletRequest);
    }
    @ApiOperation(value="accesstoken 확인", notes="accesstoken 확인 후 있으면 갱신 (response : 200 성공, 404 유저 없음)")
    @GetMapping(value="/token/id")
    public void checkAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws RuntimeException{
        tokenService.checkCookieAndRenew("accesstoken",httpServletRequest,httpServletResponse);
    }
    @ApiOperation(value="kakaoAccesstoken 확인", notes="kakaoAccesstoken 확인 후 지났으면 갱신 그 외엔 그냥 냅둠 (response : 200 성공, 404 유저 없음)")
    @GetMapping(value="/kakao/token/id")
    public void checkKakaoAccesstoken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws RuntimeException{
        kakaoService.checkExpiredTokenAndRefresh(httpServletRequest,httpServletResponse);
    }
    @ApiOperation(value="개인정보 수정",notes = "회원의 개인정보를 수정함 (response : 200 - 성공 404 - 유저 아님 409 - 유저 id 존재하지 않음 )")
    @PutMapping(value="")
    public void modify(@ApiParam(value= "수정하고자 하는 값") @RequestBody Users user,HttpServletRequest request) throws RuntimeException{
        userModifyService.modifyUser(user);
    }

    @ApiOperation(value="개인정보 수정(사진)",notes = "회원의 개인정보를 수정함(사진) (response 200 - 성공 411 - 조건에 맞는 정보가 없음 404 - 유저가 아님 409 - aws 문제 422 - 유저 id 존재하지 않음)")
    @PostMapping(value="/image/id")
    public LoggedInfo modifyImage(@RequestParam(value ="file",required=false) MultipartFile file,HttpServletRequest httpServletRequest) throws IOException,RuntimeException {
        return userModifyService.modifyImage(httpServletRequest,file);
    }

    @ApiOperation(value="유저 확인", notes = "비밀번호 확인으로 올바른 유저인지 확인 (response : 200 - 성공 411 - 비밀번호 틀림 404- user 없음)")
    @PostMapping(value="")
    public void checkCorrectUserAndPostInfo(@ApiParam(value = "비밀번호") @RequestBody Map<String,String> user,HttpServletRequest httpServletRequest)throws RuntimeException{
        userSignService.confirmCorrectUser(httpServletRequest,user.get("password"));
    }

    @ApiOperation(value="비활성화",notes = "유저가 비활성화 함 (response : 200 - 성공 411 - 맞는 정보가 없어서 비활성화 실패 404- user 없음)")
    @PutMapping(value="/id")
    public void changetoInactive(HttpServletRequest request,HttpServletResponse response) throws RuntimeException{
        userModifyService.modifyIdentify(request,response);
    }

    @ApiOperation(value = "친구 정보 가져오기",notes = "친구 정보 가져옴 (response : 200 - 성공 411 - 맞는 유저가 없음 401 - 비활성 유저")
    @GetMapping(value="/friend/{userId}")
    public LoggedInfo sendFriendInfo(@PathVariable String userId,HttpServletRequest request) throws RuntimeException{
        return userService.setLoggedInfo(userId);
    }

    @ApiOperation(value = "알람기능 활성화", notes = "알람기능 활성화 (response : 200 -성공 401- 비활성화 혹은 없는 유저 409- 저장 안됨) ")
    @PutMapping(value = "/alarm/uid")
    public void changeActiveAlarm(HttpServletRequest request) throws RuntimeException{
        alarmService.changeAlarm(true,request);
    }
    @ApiOperation(value = "알람기능 비활성화", notes ="알람기능 비활성화 (response : 200 -성공 401- 비활성화 혹은 없는 유저 409- 저장 안됨)")
    @PutMapping(value = "/alarm/uid/off")
    public void changeInactiveAlarm(HttpServletRequest request) throws RuntimeException{
        alarmService.changeAlarm(false,request);
    }

    /*@ApiOperation(value="활성화",notes = "유저가 활성화 함 (response : 200 - 성공 411 - 맞는 정보가 없어서 활성화 실패)")
    @PutMapping(value="/member/uid")
    public void changetoActive(HttpServletRequest request,HttpServletResponse response) throws RuntimeException {
        userModifyService.modifyIdentify(request, response);
    }*/
}
