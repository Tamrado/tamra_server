package com.webapp.timeline.follow.service;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.follow.service.interfaces.FriendService;
import com.webapp.timeline.follow.service.interfaces.SearchService;
import com.webapp.timeline.membership.domain.Profiles;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.interfaces.UserService;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import com.webapp.timeline.sns.dto.request.CustomPageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private TokenService tokenService;
    private UserService userService;
    private UsersEntityRepository usersEntityRepository;
    private UserImagesRepository userImagesRepository;
    private FriendService friendService;
    @Autowired
    public SearchServiceImpl(UserImagesRepository userImagesRepository,TokenService tokenService,UserService userService,UsersEntityRepository usersEntityRepository,FriendService friendService){
        this.tokenService = tokenService;
        this.userService = userService;
        this.usersEntityRepository = usersEntityRepository;
        this.friendService = friendService;
        this.userImagesRepository = userImagesRepository;
    }
    @Override
    public ArrayList<LoggedInfo> searchInFriendList(String nickname, HttpServletRequest request) throws RuntimeException{
        log.error("searchInFriendList");
        String userId = tokenService.sendIdInCookie(request);
        userService.isTrueActualUser(userId);

        List<String> friendIdList = usersEntityRepository.findNameInFirstFriendList(userId,nickname);
        friendIdList.addAll(usersEntityRepository.findNameInSecondFriendList(userId,nickname));
        ArrayList<LoggedInfo> friendListForSearching = friendService.createFriendInfo(friendIdList);
        return friendListForSearching;
    }
    @Override
    public ArrayList<LoggedInfo> searchInHeader(String nickname,CustomPageRequest request) throws RuntimeException{
        ArrayList<LoggedInfo> loggedInfoArrayList = new ArrayList<>();
        request.setSize(5);
        Page<Map<String,String>> userPage = usersEntityRepository.findUsersBySearching(nickname,request.of("userId"));
        List<Map<String,String>> userList = userPage.getContent();
        for(Map<String,String> user : userList){
            Profiles profile = userImagesRepository.findImageURLById(user.get("userId"));
            LoggedInfo loggedInfo = new LoggedInfo(user.get("userId"),profile.getProfileURL(),user.get("name"),user.get("comment"));
            loggedInfoArrayList.add(loggedInfo);
        }
        if(loggedInfoArrayList.isEmpty()) throw new NoMatchPointException();
        return loggedInfoArrayList;
    }
}
