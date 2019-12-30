package com.webapp.timeline.membership.service;


import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.exception.UnauthorizedUserException;
import com.webapp.timeline.membership.domain.Profiles;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.interfaces.UserService;
import com.webapp.timeline.membership.service.response.LoggedInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

@Transactional
@Service
public class UserServiceImpl implements UserService {
    private UserImagesRepository userImagesRepository;
    private UsersEntityRepository usersEntityRepository;
    @Autowired
    public UserServiceImpl(UserImagesRepository userImagesRepository,UsersEntityRepository usersEntityRepository) {
        this.userImagesRepository = userImagesRepository;
        this.usersEntityRepository = usersEntityRepository;
    }
    public UserServiceImpl() { }

    @Override
    public void saveImageURL(String userId,String url) throws RuntimeException {
        Profiles userImages = new Profiles();
        userImages.setId(userId);
        userImages.setProfileURL(url);
        if(userImagesRepository.selectProfileNum(userId) == 1) throw new NoStoringException();
        try {
            userImagesRepository.saveAndFlush(userImages);
        } catch (Exception e) {
            throw new NoStoringException();
        }
    }
    @Override
    public LoggedInfo setLoggedInfo(String userId) throws RuntimeException{
        Profiles profiles = userImagesRepository.findImageURLById(userId);
        Map<String,String> userInfo = usersEntityRepository.findUserInfo(userId);
        if(userInfo == null) throw new NoMatchPointException();
        if(!userInfo.get("authority").equals("ROLE_USER")) throw new UnauthorizedUserException();
        if(profiles == null) throw new NoMatchPointException();
        LoggedInfo loggedInfo = new LoggedInfo(userId, profiles.getProfileURL(),userInfo.get("name"),userInfo.get("comment"));
        return loggedInfo;
    }
    @Override
    public ArrayList<String> sendActualUserFromList(ArrayList<String> userList) throws RuntimeException{
        for(Iterator<String> it = userList.iterator(); it.hasNext() ; ){
            String id = it.next();
            Map<String,String> friendInfo= usersEntityRepository.findUserInfo(id);
            if(friendInfo.isEmpty()) continue;
            if(!friendInfo.get("authority").equals("ROLE_USER"))
                it.remove();
        }
        if(userList == null) throw new NoMatchPointException();
        return userList;
    }

    @Override
    public void isTrueActualUser(String user) throws RuntimeException{
        Map<String,String> userInfo = usersEntityRepository.findUserInfo(user);
        if(userInfo.isEmpty() || !userInfo.get("authority").equals("ROLE_USER")) throw new UnauthorizedUserException();
    }

}
