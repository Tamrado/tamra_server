package com.webapp.timeline.follow.web;

import com.webapp.timeline.follow.service.FollowService;
import com.webapp.timeline.follow.service.FriendService;
import com.webapp.timeline.follow.service.response.MyInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

}
