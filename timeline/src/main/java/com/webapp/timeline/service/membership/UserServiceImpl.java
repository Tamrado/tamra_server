package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Transactional
@Component
public class UserServiceImpl implements UserDetailsService {


    private UsersEntityRepository usersEntityRepository;
    private Users user;
    @Autowired
    public UserServiceImpl(UsersEntityRepository usersEntityRepository) {
        this.usersEntityRepository = usersEntityRepository;
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


}
