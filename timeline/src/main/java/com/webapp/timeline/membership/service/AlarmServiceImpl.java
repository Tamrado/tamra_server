package com.webapp.timeline.membership.service;

import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.interfaces.AlarmService;
import com.webapp.timeline.membership.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
public class AlarmServiceImpl implements AlarmService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private TokenService tokenService;
    private UsersEntityRepository usersEntityRepository;
    private UserService userService;

    @Autowired
    public AlarmServiceImpl(TokenService tokenService,UsersEntityRepository usersEntityRepository,UserService userService){
        this.tokenService = tokenService;
        this.userService = userService;
        this.usersEntityRepository = usersEntityRepository;
    }
    public AlarmServiceImpl(){}

    @Transactional
    @Override
    public void changeAlarm(Boolean isActive, HttpServletRequest request)throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        userService.isTrueActualUser(userId);
        try {
            if (isActive)
                usersEntityRepository.updateUserOnAlarm(userId);
            else
                usersEntityRepository.updateUserOffAlarm(userId);
        }catch(Exception e){
            log.info(e.toString());
            throw new NoStoringException();
        }
    }

    @Override
    public Boolean isTrueActiveAlarm(String userId) throws RuntimeException{
        Integer isAlarm = usersEntityRepository.selectIsAlarmFromUid(userId);
        if(isAlarm == 1) return true;
        else return false;
    }
}
