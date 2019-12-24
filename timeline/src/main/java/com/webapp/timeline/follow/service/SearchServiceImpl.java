package com.webapp.timeline.follow.service;

import com.webapp.timeline.follow.service.interfaces.FriendService;
import com.webapp.timeline.follow.service.interfaces.SearchService;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.interfaces.UserService;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
@Service
public class SearchServiceImpl implements SearchService {
    private TokenService tokenService;
    private UserService userService;
    private UsersEntityRepository usersEntityRepository;
    private FriendService friendService;
    @Autowired
    public SearchServiceImpl(TokenService tokenService,UserService userService,UsersEntityRepository usersEntityRepository,FriendService friendService){
        this.tokenService = tokenService;
        this.userService = userService;
        this.usersEntityRepository = usersEntityRepository;
        this.friendService = friendService;
    }
    @Override
    public ArrayList<LoggedInfo> searchInFriendList(String nickname, HttpServletRequest request) throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        userService.isTrueActualUser(userId);

        List<String> friendIdList = usersEntityRepository.findNameInFirstFriendList(userId,nickname);
        friendIdList.addAll(usersEntityRepository.findNameInSecondFriendList(userId,nickname));
        ArrayList<LoggedInfo> friendListForSearching = friendService.createFriendInfo(friendIdList);
        return friendListForSearching;
    }
   /* @Override
    public ArrayList<LoggedInfo> searchInHeader(String nickname) throws RuntimeException{

    }*/
}
