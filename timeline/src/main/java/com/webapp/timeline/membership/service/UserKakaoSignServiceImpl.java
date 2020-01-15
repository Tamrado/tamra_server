package com.webapp.timeline.membership.service;

import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.membership.domain.Profiles;
import com.webapp.timeline.membership.domain.RefreshToken;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.RefreshTokenRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.interfaces.KakaoService;
import com.webapp.timeline.membership.service.interfaces.UserKakaoSignService;
import com.webapp.timeline.membership.service.interfaces.UserService;
import com.webapp.timeline.membership.service.interfaces.UserSignService;
import com.webapp.timeline.membership.service.response.KakaoFirstInfo;
import com.webapp.timeline.membership.service.response.KakaoSecondInfo;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@Service
public class UserKakaoSignServiceImpl implements UserKakaoSignService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private UsersEntityRepository usersEntityRepository;
    private UserSignService userSignService;
    private UserService userService;
    private RefreshTokenRepository refreshTokenRepository;

    public UserKakaoSignServiceImpl(){}
    @Autowired
    public UserKakaoSignServiceImpl(RefreshTokenRepository refreshTokenRepository,UserService userService,UserSignService userSignService,UsersEntityRepository usersEntityRepository){
        this.usersEntityRepository = usersEntityRepository;
        this.userSignService = userSignService;
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public Boolean isUserTrue(String uid){
        String userId = usersEntityRepository.findIdByExistingId(uid);
        if(userId != null) return true;
        else return false;
    }

    @Override
    public void makeKakaoCookie(HttpServletResponse httpServletResponse,String accesstoken){
        Cookie cookie = new Cookie("kakaoAccesstoken",accesstoken);
        httpServletResponse.addCookie(cookie);
    }
    @Override
    public Boolean login(KakaoFirstInfo kakaoFirstInfo,HttpServletResponse httpServletResponse) throws RuntimeException{
        this.makeKakaoCookie(httpServletResponse,kakaoFirstInfo.getAccessToken());
        if(!this.isUserTrue(kakaoFirstInfo.getUid()+"Kakao")) this.firstSignUp(kakaoFirstInfo, httpServletResponse);
        else return false;
        return true;
    }
    @Override
    public LoggedInfo loginNext(KakaoSecondInfo kakaoSecondInfo,Long id) throws RuntimeException{
        Users user = usersEntityRepository.findUsersById(id.toString()+"Kakao");
        if(user == null) throw new NoMatchPointException();
        return this.secondSignUp(user.getUserId(),kakaoSecondInfo);
    }
    @Override
    public void firstSignUp(KakaoFirstInfo kakaoFirstInfo, HttpServletResponse httpServletResponse) throws RuntimeException{
        this.saveUserImage(kakaoFirstInfo);
        this.saveUser(kakaoFirstInfo);
        this.saveRefreshToken(kakaoFirstInfo.getUid()+"Kakao",kakaoFirstInfo.getRefreshToken());
    }

    @Transactional
    @Override
    public LoggedInfo secondSignUp(String uid,KakaoSecondInfo kakaoSecondInfo) throws RuntimeException{
        try {
            usersEntityRepository.updateSecondSignUp(kakaoSecondInfo.getEmail(), kakaoSecondInfo.getComment(), uid);
        }catch(Exception e){
            log.error(e.toString());
            throw new NoStoringException();
        }
        return userService.setLoggedInfo(uid);
    }

    @Transactional
    @Override
    public void saveRefreshToken(String uid,String token) throws RuntimeException{
        RefreshToken refreshToken = new RefreshToken(uid,token);
        try {
            refreshTokenRepository.saveAndFlush(refreshToken);
        }catch(Exception e){
            log.error(e.toString());
            throw new NoStoringException();
        }
    }
    private void saveUserImage(KakaoFirstInfo kakaoFirstInfo) throws RuntimeException{
        Profiles profile = new Profiles(kakaoFirstInfo.getUid()+"Kakao",kakaoFirstInfo.getThumbnail());
        userService.saveImageURL(profile);
    }
    private void saveUser(KakaoFirstInfo kakaoFirstInfo) throws RuntimeException{
        Users user = new Users(kakaoFirstInfo.getUid()+"Kakao",null,kakaoFirstInfo.getNickname(),null,
                kakaoFirstInfo.getEmail(),null,null,null,null);
        user.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
        user.setIsAlarm(1);
        user.setAuthority();
        userSignService.saveUser(user);
    }

}
