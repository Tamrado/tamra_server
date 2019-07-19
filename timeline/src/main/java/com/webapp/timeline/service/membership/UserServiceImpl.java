package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

@Transactional
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private UsersEntityRepository usersEntityRepository;
    @Autowired
    private void setUsersEntityRepository(UsersEntityRepository usersEntityRepository){
        this.usersEntityRepository = usersEntityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersEntityRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        return createSpringUser(user);
    }

}
