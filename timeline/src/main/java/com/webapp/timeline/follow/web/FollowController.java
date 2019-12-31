package com.webapp.timeline.follow.web;

import com.webapp.timeline.follow.service.interfaces.FollowService;
import com.webapp.timeline.follow.service.interfaces.FriendService;
import com.webapp.timeline.follow.service.interfaces.SearchService;
import com.webapp.timeline.follow.service.interfaces.UnFollowService;
import com.webapp.timeline.follow.service.response.FollowInfo;
import com.webapp.timeline.follow.service.response.PostProfileInfo;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

@Api(tags = {"2. Follow"})
@RequestMapping(value = "/api/friend")
@CrossOrigin(origins = {"*"})
@RestController
public class FollowController {
    private FollowService followService;
    private FriendService friendService;
    private UnFollowService unFollowService;

    Logger log = LoggerFactory.getLogger(this.getClass());
    public FollowController(){}
    @Autowired
    public FollowController(UnFollowService unFollowService,FollowService followService, FriendService friendService){
        this.followService = followService;
        this.friendService = friendService;
        this.unFollowService= unFollowService;
    }
    @ApiOperation(value = "내 follow follower 개수" , notes = "마이 포스트 내 개수 리턴 (response : 200 - 성공, 404 - 아이디가 없는 아이디임 401 - 유저가 비활함 )")
    @GetMapping(value="/post/num")
    public FollowInfo sendMyFollowFollowerNum(HttpServletRequest httpServletRequest) throws RuntimeException {
        return followService.sendMyInfo(httpServletRequest);
    }
    @ApiOperation(value = "다른이의 follow follower 개수" , notes = "마이 포스트 내 개수 리턴 (response : 200 - 성공, 404 - 아이디가 없는 아이디임 401 - 유저가 비활함 )")
    @GetMapping(value="/post/{fid}/num")
    public PostProfileInfo sendFriendFollowFollowerNum(@PathVariable String fid,HttpServletRequest request) throws RuntimeException {
        return followService.sendFriendInfo(fid);
    }

    @ApiOperation(value = "unfollow 버튼 클릭",notes = "response : 200 - 성공 404 - userId 존재하지 않는 아이디" +
            " 411 - 친구 아이디가 존재하지 않음 401 - 친구나 유저가 비활함 409 - 저장되지 않음")
    @PutMapping(value = "/no")
    public void clickUnfollow(@RequestBody Map<String,String> friendId, HttpServletRequest request) throws RuntimeException{
        unFollowService.sendUnFollow(friendId.get("friendId"), request);
    }

    @ApiOperation(value = "follow 버튼 클릭" , notes = "팔로우 신청 (response : 200 -성공 404 - userId 존재하지 않는 아이디" +
            " 409 - follow 잘못해서 취소했는데 상대방이 바로 follow 한 경우 411 - 친구 아이디가 존재하지 않음" +
            " 401 - 친구나 유저가 비활함)")
    @GetMapping(value="")
    public void clickFollow(String friendName,HttpServletRequest httpServletRequest) throws RuntimeException{
        followService.sendFollow(friendName,httpServletRequest);
    }

    @ApiOperation(value = "친구 신청 알람탭 내 컨텐츠", notes = "list 컨텐츠 (response : 200 - 성공 404 - user가 없는 유저 411 - friend가 없는 유저 401 - 유저가 비활함)")
    @GetMapping(value= "/alarmlist")
    public ArrayList<LoggedInfo> sendFriendAlarmContents(HttpServletRequest httpServletRequest) throws RuntimeException{
        return friendService.sendFriendApplyList(httpServletRequest);
    }

    @ApiOperation(value = "친구 신청 알림 삭제", notes = "response : 200- 성공 404 - uid 없음 411 - fid 없음 401 - 유저가 비활함")
    @PutMapping(value = "/alarmlist/notification")
    public void deleteFriendApplyAlarm(HttpServletRequest httpServletRequest,@RequestBody Map<String,String> friendName) throws RuntimeException{
        friendService.invalidateFriendApplyAlarm(httpServletRequest,friendName.get("userId"));
    }
    @ApiOperation(value = "친구 리스트 컨텐츠", notes = "response : 200 성공 404 - uid 없음 411 - 친구가 없음 401 - 유저가 비활함")
    @GetMapping(value = "/list")
    public ArrayList<LoggedInfo> sendFriendListContents(HttpServletRequest httpServletRequest) throws RuntimeException{
        log.info("친구 리스트 컨텐츠");
        return friendService.sendFriendList(httpServletRequest);
    }

    @ApiOperation(value = "유저가 팔로우 하는 사람인지 아닌지", notes = "response: 200 성공 411 - 팔로우 하는 사람이 아님 401 - 비활성한 유저 ")
    @GetMapping(value = "/{fid}/isfollow")
    public void getIsFollowUser(HttpServletRequest request,@PathVariable String fid) throws RuntimeException{
        followService.sendIsFollowingUser(request, fid);
    }

}

