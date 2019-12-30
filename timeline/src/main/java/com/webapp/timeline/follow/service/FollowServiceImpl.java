package com.webapp.timeline.follow.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
public class FollowServiceImpl implements FollowService {
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
    public FollowInfo sendMyInfo(HttpServletRequest request) throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        userService.isTrueActualUser(userId);

        FollowInfo followInfo = sendFollowFollowerNum(userId);
        return followInfo;
    }
    @Override
    public PostProfileInfo sendFriendInfo(String fid) throws RuntimeException{
        PostProfileInfo postProfileInfo = new PostProfileInfo();
        postProfileInfo.setUserInfo(userService.setLoggedInfo(fid));
        postProfileInfo.setFollowInfo(sendFollowFollowerNum(fid));
        return postProfileInfo;
    }
    private FollowInfo sendFollowFollowerNum(String uid){
        FollowInfo followInfo = new FollowInfo();
        int followerNum = 0,followNum = 0;
        followerNum += followersRepository.findFollowerNum(uid);
        followerNum += followingRepository.findFollowerNum(uid);
        followNum += followersRepository.findFollowNum(uid);
        followNum += followingRepository.findFollowNum(uid);
        followInfo.setFollowNum(followNum);
        followInfo.setFollowerNum(followerNum);
        return followInfo;
    }
    @Override
    @Transactional
    public void sendFollow(String fid,HttpServletRequest request)throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        userService.isTrueActualUser(userId);
        String friend = usersEntityRepository.findIdByExistingId(fid);
        if(friend == null) throw new NoMatchPointException();
        userService.isTrueActualUser(friend);
        if(followersRepository.isThisMyFollower(userId,fid) == 0){
            FollowId followingId = new FollowId(userId,fid);
            Followings followings = new Followings(followingId,0);
            FollowId followerId = new FollowId(fid,userId);
            Followers followers = new Followers(followerId,1);
            try {
                followingRepository.saveAndFlush(followings);
                followersRepository.saveAndFlush(followers);
            }catch(Exception e){
               throw new NoStoringException();
            }
        }
        else friendService.matchNewRelationship(userId,fid);
    }
    public void sendIsFollowingUser(HttpServletRequest request,String fid)throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        userService.isTrueActualUser(userId);
        userService.isTrueActualUser(fid);
        String friendOne = followingRepository.selectIsFollowingUser(userId,fid);
        String friendTwo = followersRepository.selectIsFollowingUser(userId,fid);
        if(friendOne == null && friendTwo == null)
            throw new NoMatchPointException();
    }
}