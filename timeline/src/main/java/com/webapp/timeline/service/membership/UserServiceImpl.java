package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;

import com.webapp.timeline.security.CustomPasswordEncoder;
import com.webapp.timeline.security.JwtTokenProvider;
import com.webapp.timeline.security.SignUpValidator;
import com.webapp.timeline.service.result.CommonResult;
import com.webapp.timeline.service.result.SingleResult;
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
    private JwtTokenProvider jwtTokenProvider;
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
        user.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
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
    public SingleResult<String> findUser(String id, String password) {
        Users foundedUser = loadUserByUsername(id);
        SingleResult<String> singleResult = new SingleResult<String>();
        if(customPasswordEncoder.matches(password, foundedUser.getPassword())) {
            jwtTokenProvider = new JwtTokenProvider(new UserServiceImpl());
            String jwt = jwtTokenProvider.createToken(foundedUser.getId(),foundedUser.getPassword());
            singleResult.setCode(1);
            singleResult.setSuccess(true);
            singleResult.setMsg("same user");
            singleResult.setData(jwt);
        }
        else{
            singleResult.setCode(-1);
            singleResult.setSuccess(false);
            singleResult.setMsg("wrong user");
            singleResult.setData(null);
        }
        return singleResult;
    }

}
