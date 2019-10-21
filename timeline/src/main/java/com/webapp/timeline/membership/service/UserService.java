package com.webapp.timeline.membership.service;


import com.webapp.timeline.membership.domain.Profiles;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.CustomPasswordEncoder;
import com.webapp.timeline.membership.security.JwtTokenProvider;
import com.webapp.timeline.membership.service.result.CommonResult;
import com.webapp.timeline.membership.service.result.LoggedInfo;
import com.webapp.timeline.membership.service.result.SingleResult;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CommonResult confirmCorrectUser(String password) {
        log.error("UserService.confirmCorrectUser");
        Users user = extractUserFromToken();
        CommonResult commonResult = new CommonResult();
        if (customPasswordEncoder.matches(password, user.getPassword()))
            commonResult.setSuccessResult(200,"correct user");
        else
            commonResult.setMsg("wrong user");
        return commonResult;
    }

    public Users extractUserFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users correctUser = (Users) authentication.getPrincipal();
        return correctUser;
    }

    public SingleResult<String> saveImageURL(SingleResult<String> singleResult, String userId) {
        Profiles userImages = new Profiles();
        userImages.setId(userId);
        userImages.setprofileURL(singleResult.getData());
        try {
            userImagesRepository.saveAndFlush(userImages);

        } catch (Exception e) {
            singleResult.setFailResult(400, "fail to save userImage", e.toString());
        }
        if(singleResult.getSuccess()) singleResult.setMsg("success to save userImage");
        return singleResult;
    }
    public LoggedInfo setLoggedInfo(SingleResult<String> singleResult, String userId){
        Profiles profiles = userImagesRepository.findImageURLById(userId);
        LoggedInfo loggedInfo = new LoggedInfo(userId,profiles.getprofileURL());
        return loggedInfo;
    }


}
