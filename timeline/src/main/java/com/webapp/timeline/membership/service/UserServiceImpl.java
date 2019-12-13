package com.webapp.timeline.membership.service;


import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.membership.domain.Profiles;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.response.LoggedInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String nickname = usersEntityRepository.findNickname(userId);
        if(profiles == null) throw new NoMatchPointException();
        LoggedInfo loggedInfo = new LoggedInfo(userId, profiles.getProfileURL(),nickname);
        return loggedInfo;
    }
}
