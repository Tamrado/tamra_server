package com.webapp.timeline.membership.service;


import com.webapp.timeline.exception.NoInformationException;
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
    public void isThereAnyProfileId(String userId) throws RuntimeException{
        if(userImagesRepository.selectProfileNum(userId) == 1) throw new NoStoringException();
    }
    @Override
    public void saveImageURL(Profiles profile) throws RuntimeException{
        try {
            userImagesRepository.saveAndFlush(profile);
        } catch (Exception e) {
            throw new NoStoringException();
        }
    }
    @Override
    public LoggedInfo setLoggedInfo(String userId) throws RuntimeException{
        Profiles profiles = userImagesRepository.findImageURLById(userId);
        Map<String,String> userInfo = usersEntityRepository.findUserInfo(userId);
        if(userInfo.isEmpty()) throw new NoMatchPointException();
        if(!userInfo.get("authority").equals("ROLE_USER")) throw new UnauthorizedUserException();
        if(profiles == null) throw new NoMatchPointException();
        LoggedInfo loggedInfo = new LoggedInfo(userId, profiles.getProfileURL(),userInfo.get("name"),userInfo.get("comment"),this.sendTokenCategory(userId));
        return loggedInfo;
    }
    @Override
    public ArrayList<String> sendActualUserFromList(ArrayList<String> userList) throws RuntimeException{
        if(userList.isEmpty()) throw new NoMatchPointException();
        for(Iterator<String> it = userList.iterator(); it.hasNext() ; ){
            String id = it.next();
            Map<String,String> friendInfo= usersEntityRepository.findUserInfo(id);
            if(!friendInfo.isEmpty() &&!friendInfo.get("authority").equals("ROLE_USER"))
                it.remove();
        }
        return userList;
    }

    @Override
    public void isTrueActualUser(String user) throws RuntimeException{
        if(user == null) throw new NoInformationException();
        Map<String,String> userInfo = usersEntityRepository.findUserInfo(user);
        if(userInfo.isEmpty() || !userInfo.get("authority").equals("ROLE_USER")) throw new UnauthorizedUserException();
    }

    @Override
    public String sendTokenCategory(String userId){
        if(userId.contains("Kakao")) return "Kakao";
        else return "basic";
    }


}
