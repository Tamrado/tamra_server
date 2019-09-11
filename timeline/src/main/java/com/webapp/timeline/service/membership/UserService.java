package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Profiles;
import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UserImagesRepository;
import com.webapp.timeline.repository.UsersEntityRepository;
import com.webapp.timeline.security.CustomPasswordEncoder;
import com.webapp.timeline.service.result.CommonResult;
import com.webapp.timeline.service.result.SingleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class UserService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private UsersEntityRepository usersEntityRepository;
    private UserImagesRepository userImagesRepository;
    private CustomPasswordEncoder customPasswordEncoder;

    @Autowired
    public UserService(CustomPasswordEncoder customPasswordEncoder, UsersEntityRepository usersEntityRepository, UserImagesRepository userImagesRepository) {
        this.customPasswordEncoder = customPasswordEncoder;
        this.usersEntityRepository = usersEntityRepository;
        this.userImagesRepository = userImagesRepository;
    }
    public UserService() {

    }

    public CommonResult saveUser(Users user) {
        CommonResult commonResult = new CommonResult();
        try {
            usersEntityRepository.save(user);
            usersEntityRepository.flush();
            commonResult.setSuccessResult(200,"success saving user");
        } catch (Exception e) {
            commonResult.setMsg("fail to save");
        }
        return commonResult;
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

    public SingleResult<String> saveImageURL(SingleResult<String> singleResult,String userId) {
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
}
