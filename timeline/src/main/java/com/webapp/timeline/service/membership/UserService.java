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

    public SingleResult<Long> saveUser(Users user) {
        SingleResult<Long> singleResult = new SingleResult<>();
        try {
            usersEntityRepository.save(user);
            usersEntityRepository.flush();
            singleResult.setMsg("success saving user");
            singleResult.setData(user.getMasterId());
            singleResult.setSuccess(true);
            singleResult.setCode(200);
        } catch (Exception e) {
            singleResult.setMsg("fail to save");
        }
        return singleResult;
    }

    public CommonResult confirmCorrectUser(String password) {
        log.error("UserService.confirmCorrectUser");
        Users user = extractUserFromToken();
        CommonResult commonResult = new CommonResult();
        if (customPasswordEncoder.matches(password, user.getPassword())) {
            commonResult.setCode(200);
            commonResult.setSuccess(true);
            commonResult.setMsg("correct user");
        } else
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
            singleResult.setCode(400);
            singleResult.setSuccess(false);
            singleResult.setData(null);
            singleResult.setMsg("fail to save userImage");
        }
        if(singleResult.getSuccess()) singleResult.setMsg("success to save userImage");
        return singleResult;
    }
}
