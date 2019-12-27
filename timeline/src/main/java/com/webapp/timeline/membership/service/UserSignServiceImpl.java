package com.webapp.timeline.membership.service;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.CustomPasswordEncoder;
import com.webapp.timeline.membership.security.JwtTokenProvider;
import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.interfaces.UserSignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Configurable
@Service
public class UserSignServiceImpl implements UserDetailsService, UserSignService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private UsersEntityRepository usersEntityRepository;
    private CustomPasswordEncoder customPasswordEncoder;
    private SignUpValidator signUpValidator;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserSignServiceImpl(SignUpValidator signUpValidator, UsersEntityRepository usersEntityRepository, CustomPasswordEncoder customPasswordEncoder) {
        this.signUpValidator = signUpValidator;
        this.usersEntityRepository = usersEntityRepository;
        this.customPasswordEncoder = customPasswordEncoder;
    }
    public UserSignServiceImpl(){ }

    @Override
    public Users loadUserByUsername(String username) throws RuntimeException {
        log.info("loadUserByUsername");
        try {
            Users user = usersEntityRepository.findUsersById(username);
            return user;
        }
        catch(Exception e){
            log.info(e.toString());
            throw new NoInformationException();
        }

    }
    @Override
    public Users extractUserFromToken(HttpServletRequest httpServletRequest) throws RuntimeException{
        jwtTokenProvider = new JwtTokenProvider(new UserSignServiceImpl());
        String username = jwtTokenProvider.extractUserIdFromToken(jwtTokenProvider.resolveToken(httpServletRequest));
        Users user = loadUserByUsername(username);
        return user;
    }
    @Override
    public void confirmCorrectUser(HttpServletRequest httpServletRequest ,String password) throws RuntimeException {
        log.error("UserService.confirmCorrectUser");
        Users user = extractUserFromToken(httpServletRequest);
        if (!customPasswordEncoder.matches(password, user.getPassword()))
            throw new NoMatchPointException();
    }

    @Override
    public void validateUser(Users users) throws RuntimeException{
        signUpValidator.validate(users);
        initUserforSignUp(users);
    }

    @Override
    public void initUserforSignUp(Users user) throws RuntimeException{
        user.setPassword(customPasswordEncoder.encode(user.getPassword()));
        user.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
        user.setAuthority();
        saveUser(user);
    }
    @Override
    public void findUser(Map<String,Object> user) throws RuntimeException {
        Users foundedUser = loadUserByUsername(user.get("id").toString());
        log.info(foundedUser.getPassword());
        if(!customPasswordEncoder.matches(user.get("password").toString(), foundedUser.getPassword()))
            throw new NoMatchPointException();
    }
    @Transactional
    @Override
    public void saveUser(Users user) throws RuntimeException {
        try {
            usersEntityRepository.save(user);
            usersEntityRepository.flush();
        } catch (Exception e) {
         throw new NoStoringException();
        }
    }

}
