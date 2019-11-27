package com.webapp.timeline.membership.service;


import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.membership.domain.Profiles;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.CustomPasswordEncoder;
import com.webapp.timeline.membership.service.result.LoggedInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Transactional
@Service
public class UserServiceImpl implements UserService {
    private UserImagesRepository userImagesRepository;

    @Autowired
    public UserServiceImpl(UserImagesRepository userImagesRepository) {
        this.userImagesRepository = userImagesRepository;
    }
    public UserServiceImpl() { }

    @Override
    public void saveImageURL(String userId,String url) throws RuntimeException {
        Profiles userImages = new Profiles();
        userImages.setId(userId);
        userImages.setprofileURL(url);
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
        if(profiles == null) throw new NoMatchPointException();
        LoggedInfo loggedInfo = new LoggedInfo(userId, profiles.getprofileURL());
        return loggedInfo;
    }
}
