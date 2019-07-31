package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Component
public class UserServiceImpl implements UserDetailsService {

    private PasswordEncoder passwordEncoder;
    private String userId;
    private Long masterId;
    private UsersEntityRepository usersEntityRepository;
    private Users user;
    @Autowired
    private UserServiceImpl(PasswordEncoder passwordEncoder,UsersEntityRepository usersEntityRepository) {
        this.usersEntityRepository = usersEntityRepository;
        this.passwordEncoder = passwordEncoder;
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
