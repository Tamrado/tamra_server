package com.webapp.timeline.membership.service;


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
public class UserService {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private UserImagesRepository userImagesRepository;
    private CustomPasswordEncoder customPasswordEncoder;
    private UsersEntityRepository usersEntityRepository;

    @Autowired
    public UserService(CustomPasswordEncoder customPasswordEncoder, UserImagesRepository userImagesRepository,UsersEntityRepository usersEntityRepository) {
        this.customPasswordEncoder = customPasswordEncoder;
        this.userImagesRepository = userImagesRepository;
        this.usersEntityRepository = usersEntityRepository;

    }
    public UserService() {

    }


    public void saveImageURL(String userId, HttpServletResponse response,String url) {
        Profiles userImages = new Profiles();
        userImages.setId(userId);
        userImages.setprofileURL(url);
        try {
            userImagesRepository.saveAndFlush(userImages);
            response.setStatus(200);
        } catch (Exception e) {
            response.setStatus(400);
        }
    }
    public LoggedInfo setLoggedInfo(HttpServletResponse response,String userId){
        if(response.getStatus() != 404) {
            Profiles profiles = userImagesRepository.findImageURLById(userId);
            if(profiles != null) {
                LoggedInfo loggedInfo = new LoggedInfo(userId, profiles.getprofileURL());
                return loggedInfo;
            }
            else{
                response.setStatus(404);
                return null;
            }
        }
        else return null;
    }
}
