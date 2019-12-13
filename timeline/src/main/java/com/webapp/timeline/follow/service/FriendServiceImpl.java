package com.webapp.timeline.follow.service;

import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.follow.repository.FollowersRepository;
import com.webapp.timeline.follow.repository.FollowingRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
public class FriendServiceImpl implements FriendService{
    FollowingRepository followingRepository;
    FollowersRepository followersRepository;
    UsersEntityRepository usersEntityRepository;
    public FriendServiceImpl(){}
    @Autowired
    public FriendServiceImpl(UsersEntityRepository usersEntityRepository,FollowersRepository followersRepository, FollowingRepository followingRepository){
        this.followersRepository = followersRepository;
        this.followingRepository = followingRepository;
        this.usersEntityRepository = usersEntityRepository;
    }
    @Override
    @Transactional
    public void matchNewRelationship(String uid,String fid) throws RuntimeException{
        try {
            followingRepository.updateNewFriend(uid, fid);
        }catch(Exception e){
            throw new NoStoringException();
        }
    }
    @Override
    public ArrayList<LoggedInfo> sendFriendApplyList(HttpServletRequest request) throws RuntimeException{
        ArrayList<LoggedInfo> loggedInfoArrayList = new ArrayList<>();
        return loggedInfoArrayList;
    }
}
