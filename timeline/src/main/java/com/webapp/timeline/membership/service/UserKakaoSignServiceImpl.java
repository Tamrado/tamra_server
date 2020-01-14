package com.webapp.timeline.membership.service;

import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.membership.domain.RefreshToken;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.RefreshTokenRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.interfaces.UserKakaoSignService;
import com.webapp.timeline.membership.service.interfaces.UserService;
import com.webapp.timeline.membership.service.interfaces.UserSignService;
import com.webapp.timeline.membership.service.response.KakaoFirstInfo;
import com.webapp.timeline.membership.service.response.KakaoSecondInfo;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@Service
public class UserKakaoSignServiceImpl implements UserKakaoSignService {
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
    public void login(KakaoFirstInfo kakaoFirstInfo,HttpServletResponse httpServletResponse) throws RuntimeException{
       Users user = usersEntityRepository.findUsersById(kakaoFirstInfo.getUid());
       if(user == null) this.firstSignUp(kakaoFirstInfo, httpServletResponse);
       else{
           Cookie cookie = new Cookie("kakaoAccesstoken",kakaoFirstInfo.getAccessToken());
           httpServletResponse.addCookie(cookie);
       }
    }
    @Override
    public LoggedInfo loginNext(KakaoSecondInfo kakaoSecondInfo,Long id) throws RuntimeException{
        Users user = usersEntityRepository.findUsersById(id.toString()+"Kakao");
        if(user == null) throw new NoMatchPointException();
        return this.secondSignUp(user,kakaoSecondInfo);
    }
    @Override
    public void firstSignUp(KakaoFirstInfo kakaoFirstInfo, HttpServletResponse httpServletResponse) throws RuntimeException{
        Users user = new Users(kakaoFirstInfo.getUid()+"Kakao",null,kakaoFirstInfo.getNickname(),null,
                kakaoFirstInfo.getEmail(),null,null,null,null);
        RefreshToken refreshToken = new RefreshToken(kakaoFirstInfo.getUid()+"Kakao",kakaoFirstInfo.getRefreshToken());
        userSignService.saveUser(user);
        this.saveRefreshToken(refreshToken);
    }

    @Transactional
    @Override
    public LoggedInfo secondSignUp(Users user,KakaoSecondInfo kakaoSecondInfo) throws RuntimeException{
        user.setComment(kakaoSecondInfo.getComment());
        if(user.getEmail() == null) user.setEmail(kakaoSecondInfo.getEmail());
        userSignService.saveUser(user);
        return userService.setLoggedInfo(user.getUserId());
    }

    @Transactional
    @Override
    public void saveRefreshToken(RefreshToken refreshToken) throws RuntimeException{
        try {
            refreshTokenRepository.save(refreshToken);
            refreshTokenRepository.flush();
        }catch(Exception e){
            throw new NoStoringException();
        }
    }

}
