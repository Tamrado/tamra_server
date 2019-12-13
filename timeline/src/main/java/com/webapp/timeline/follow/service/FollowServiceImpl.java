package com.webapp.timeline.follow.service;

import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.follow.domain.FollowId;
import com.webapp.timeline.follow.domain.Followers;
import com.webapp.timeline.follow.domain.Followings;
import com.webapp.timeline.follow.repository.FollowersRepository;
import com.webapp.timeline.follow.repository.FollowingRepository;
import com.webapp.timeline.follow.service.response.MyInfo;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FollowServiceImpl implements FollowService {
    FollowersRepository followersRepository;
    FollowingRepository followingRepository;
    TokenService tokenService;
    FriendService friendService;
    UsersEntityRepository usersEntityRepository;
    public FollowServiceImpl(){}
    @Autowired
    public FollowServiceImpl(FriendService friendService,UsersEntityRepository usersEntityRepository,TokenService tokenService,FollowersRepository followersRepository, FollowingRepository followingRepository){
        this.followersRepository = followersRepository;
        this.followingRepository = followingRepository;
        this.tokenService = tokenService;
        this.friendService = friendService;
        this.usersEntityRepository = usersEntityRepository;
    }
    @Override
    public MyInfo sendUserInfo(HttpServletRequest request) throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        MyInfo myInfo = sendFollowFollowerNum(userId);
        myInfo.setComment(usersEntityRepository.findComment(userId));
        return myInfo;
    }
    private MyInfo sendFollowFollowerNum(String uid){
        MyInfo myInfo = new MyInfo();
        int followerNum = 0,followNum = 0;
        followerNum += followersRepository.findFollowerNum(uid);
        followerNum += followingRepository.findFollowerNum(uid);
        followNum += followersRepository.findFollowNum(uid);
        followNum += followingRepository.findFollowNum(uid);
        myInfo.setFollowNum(followNum);
        myInfo.setFollowerNum(followerNum);
        return myInfo;
    }
    @Override
    @Transactional
    public void sendFollow(String fid,HttpServletRequest request)throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        String friend = usersEntityRepository.findIdByExistingId(fid);
        if(friend == null) throw new NoMatchPointException();
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
}