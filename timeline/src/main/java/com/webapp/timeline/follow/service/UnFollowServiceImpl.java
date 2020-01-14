package com.webapp.timeline.follow.service;

import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.follow.repository.FollowersRepository;
import com.webapp.timeline.follow.repository.FollowingRepository;
import com.webapp.timeline.follow.service.interfaces.FollowService;
import com.webapp.timeline.follow.service.interfaces.FriendService;
import com.webapp.timeline.follow.service.interfaces.UnFollowService;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
@Service
public class UnFollowServiceImpl implements UnFollowService {
    private TokenService tokenService;
    private UserService userService;
    private UsersEntityRepository usersEntityRepository;
    private FollowingRepository followingRepository;
    private FollowersRepository followersRepository;
    private FriendService friendService;
    @Autowired
    public UnFollowServiceImpl(FriendService friendService,TokenService tokenService,UserService userService,UsersEntityRepository usersEntityRepository,FollowersRepository followersRepository,FollowingRepository followingRepository){
        this.followersRepository = followersRepository;
        this.followingRepository = followingRepository;
        this.tokenService = tokenService;
        this.userService = userService;
        this.usersEntityRepository =usersEntityRepository;
        this.friendService = friendService;
    }
    public UnFollowServiceImpl(){}
    @Transactional
    @Override
    public void sendUnFollow(String fid, HttpServletRequest request) throws RuntimeException{
        String userId = friendService.sendLoginUserId(request).get();
        String friend = usersEntityRepository.findIdByExistingId(fid);
        if(friend == null) throw new NoMatchPointException();
        userService.isTrueActualUser(fid);
        try {
            followersRepository.updateUnfollow(userId, fid);
            followingRepository.updateUnfollow(userId, fid);
        }catch(Exception e){
            throw new NoStoringException();
        }
    }
}
