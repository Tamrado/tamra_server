package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service("signUpService")
public class signUpServiceImpl implements CheckDetailedSignUpService {

    private Boolean checkingIdOverlap;
    private
    @Autowired
    private UsersEntityRepository userEntity;
    @Override
    public Boolean checkIfExistingIdOverlap(Users user){
        Users existingId = userEntity.findIdByExistingId(user.getId());
        if(existingId.getId().getUserId().equals(user.getId()))
            checkingIdOverlap = true;
        return checkingIdOverlap;
    }
    @override
    public void insertId
    @Override
    public Boolean checkGroupOverlap(Users user){

    }

}
