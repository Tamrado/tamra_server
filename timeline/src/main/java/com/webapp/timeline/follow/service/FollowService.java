package com.webapp.timeline.follow.service;

import com.webapp.timeline.follow.repository.FollowersRepository;
import com.webapp.timeline.follow.repository.FollowingRepository;

import com.webapp.timeline.follow.service.response.MyInfo;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class FollowService {
    FollowersRepository followersRepository;
    FollowingRepository followingRepository;
    TokenService tokenService;
    UsersEntityRepository usersEntityRepository;
    public FollowService(){}
    @Autowired
    public FollowService(UsersEntityRepository usersEntityRepository,TokenService tokenService,FollowersRepository followersRepository, FollowingRepository followingRepository){
        this.followersRepository = followersRepository;
        this.followingRepository = followingRepository;
        this.tokenService = tokenService;
        this.usersEntityRepository = usersEntityRepository;
    }

    public MyInfo sendUserInfo(HttpServletRequest request) throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        MyInfo myInfo = sendFollowFollowerNum(userId);
        myInfo.setComment(usersEntityRepository.findComment(userId));
        return myInfo;
    }
    public MyInfo sendFollowFollowerNum(String uid){
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
}
