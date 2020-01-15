package com.webapp.timeline.follow.service;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.follow.domain.FollowId;
import com.webapp.timeline.follow.domain.Followers;
import com.webapp.timeline.follow.domain.Followings;
import com.webapp.timeline.follow.repository.FollowersRepository;
import com.webapp.timeline.follow.repository.FollowingRepository;
import com.webapp.timeline.follow.service.interfaces.FollowService;
import com.webapp.timeline.follow.service.interfaces.FriendService;
import com.webapp.timeline.follow.service.response.FollowInfo;
import com.webapp.timeline.follow.service.response.PostProfileInfo;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    FollowersRepository followersRepository;
    FollowingRepository followingRepository;
    TokenService tokenService;
    FriendService friendService;
    UserService userService;
    UsersEntityRepository usersEntityRepository;
    public FollowServiceImpl(){}
    @Autowired
    public FollowServiceImpl(UserService userService,FriendService friendService,UsersEntityRepository usersEntityRepository,TokenService tokenService,FollowersRepository followersRepository, FollowingRepository followingRepository){
        this.followersRepository = followersRepository;
        this.userService = userService;
        this.followingRepository = followingRepository;
        this.tokenService = tokenService;
        this.friendService = friendService;
        this.usersEntityRepository = usersEntityRepository;
    }
    @Override
    public FollowInfo sendMyInfo(HttpServletRequest request) throws RuntimeException {
        log.info("FollowServiceImpl.sendMyInfo::::");
        String userId = friendService.sendLoginUserId(request).get();
        FollowInfo followInfo = sendFollowerNum(userId,sendFollowNum(userId));
        return followInfo;
    }
    @Override
    public PostProfileInfo sendFriendInfo(String fid) throws RuntimeException{
        PostProfileInfo postProfileInfo = new PostProfileInfo();
        postProfileInfo.setUserInfo(userService.setLoggedInfo(fid));
        postProfileInfo.setFollowInfo(sendFollowerNum(fid,sendFollowNum(fid)));
        return postProfileInfo;
    }
    private FollowInfo sendFollowNum(String uid){
        FollowInfo followInfo = new FollowInfo();
        int followNum = 0;
        followNum += followersRepository.findFollowNum(uid);
        followNum += followingRepository.findFollowNum(uid);
        followInfo.setFollowNum(followNum);
        return followInfo;
    }
    private FollowInfo sendFollowerNum(String uid,FollowInfo followInfo){
        int followerNum = 0;
        followerNum += followersRepository.findFollowerNum(uid);
        followerNum += followingRepository.findFollowerNum(uid);

        followInfo.setFollowerNum(followerNum);
        return followInfo;
    }
    @Override
    @Transactional
    public void sendFollow(String fid,HttpServletRequest request)throws RuntimeException{
        String userId = friendService.sendLoginUserId(request).get();
        String friend = usersEntityRepository.findIdByExistingId(fid);
        userService.isTrueActualUser(friend);
        if(followersRepository.isThisMyFollower(userId,fid) == 0){
            this.makeFollowInFollowers(userId,fid);
            this.makeFollowInFollowings(userId,fid);
        }
        else friendService.matchNewRelationship(userId,fid);
    }
    private void makeFollowInFollowings(String userId,String fid) throws RuntimeException{
        FollowId followingId = new FollowId(userId,fid);
        Followings followings = new Followings(followingId,0);
        try {
            followingRepository.saveAndFlush(followings);
        }catch(Exception e){
            throw new NoStoringException();
        }
    }
    private void makeFollowInFollowers(String userId, String fid) throws RuntimeException {
        FollowId followerId = new FollowId(fid,userId);
        Followers followers = new Followers(followerId,1);
        try {
            followersRepository.saveAndFlush(followers);
        }catch(Exception e){
            throw new NoStoringException();
        }
    }
    @Override
    public void sendIsFollowingUser(HttpServletRequest request,String fid)throws RuntimeException{
        String userId = friendService.sendLoginUserId(request).get();
        userService.isTrueActualUser(fid);
        String friendOne = followingRepository.selectIsFollowingUser(userId,fid);
        String friendTwo = followersRepository.selectIsFollowingUser(userId,fid);
        if(friendOne == null && friendTwo == null)
            throw new NoMatchPointException();
    }

}