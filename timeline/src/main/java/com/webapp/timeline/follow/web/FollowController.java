package com.webapp.timeline.follow.web;

import com.webapp.timeline.follow.service.FollowService;
import com.webapp.timeline.follow.service.FriendService;
import com.webapp.timeline.follow.service.response.MyInfo;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Api(tags = {"2. Follow"})
@RequestMapping(value = "/api")
@CrossOrigin(origins = {"*"})
@RestController
public class FollowController {
    FollowService followService;
    FriendService friendService;
    public FollowController(){}
    @Autowired
    public FollowController(FollowService followService, FriendService friendService){
        this.followService = followService;
        this.friendService = friendService;
    }
    @ApiOperation(value = "follow follower 개수" , notes = "마이 포스트 내 개수 리턴 (response : 200 - 성공, 404 - 아이디가 없는 아이디임 )")
    @GetMapping(value="/friend/num")
    public MyInfo sendFollowFollowerNum(HttpServletRequest httpServletRequest) throws RuntimeException {
        return followService.sendUserInfo(httpServletRequest);
    }

    @ApiOperation(value = "follow 버튼 클릭" , notes = "팔로우 신청 (response : 200 -성공 404 - userId 존재하지 않는 아이디 409 - 존재하지 않음 411 - 친구 아이디가 존재하지 않음)")
    @GetMapping(value="/friend")
    public void clickFollow(String friendName,HttpServletRequest httpServletRequest) throws RuntimeException{
        followService.sendFollow(friendName,httpServletRequest);
    }

    @ApiOperation(value = "친구 신청 알람탭 내 컨텐츠", notes = "list 컨텐츠 (response : 200 - 성공)")
    @GetMapping(value= "/friend/alarmlist")
    public ArrayList<LoggedInfo> sendFriendAlarmContents(HttpServletRequest httpServletRequest) throws RuntimeException{
        return friendService.sendFriendApplyList(httpServletRequest);
    }

}
