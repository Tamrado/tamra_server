package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;

import com.webapp.timeline.security.CustomPasswordEncoder;
import com.webapp.timeline.security.SignUpValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Transactional
@Component
public class UserServiceImpl implements UserDetailsService {
    private UsersEntityRepository usersEntityRepository;
    private CustomPasswordEncoder customPasswordEncoder;
    private Users user;
    @Autowired
    public UserServiceImpl(UsersEntityRepository usersEntityRepository, CustomPasswordEncoder customPasswordEncoder) {
        this.usersEntityRepository = usersEntityRepository;
        this.customPasswordEncoder = customPasswordEncoder;
    }
    public UserServiceImpl(){

    }

    @Override
    public Users loadUserByUsername(String username) throws UsernameNotFoundException {
        user = usersEntityRepository.findIdByExistingId(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        user.setAuthorities();
        return user;
    }
    public CommonResult validateUser(Users users){
        SignUpValidator signUpValidator = new SignUpValidator(usersEntityRepository);
        CommonResult commonResult = signUpValidator.validate(users);
        if(!commonResult.getSuccess()) return commonResult;
         commonResult = saveUser(users,commonResult);
        return commonResult;
    }
    public CommonResult saveUser(Users user,CommonResult commonResult){
        user.setPassword(customPasswordEncoder.encode(user.getPassword()));
        user.setAuthorities();
        try {
            usersEntityRepository.save(user);
            usersEntityRepository.flush();
        }catch (Exception e){
            commonResult.setCode(-1);
            commonResult.setSuccess(false);
            commonResult.setMsg("fail to save");
            return commonResult;
        }
        return commonResult;
    }


}
