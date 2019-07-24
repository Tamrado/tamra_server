package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Transactional
@Service
public class CheckSignUpServiceImpl implements CheckSignUpService {

    private Boolean idCheckingOverlapping = false;
    private Boolean phoneCheckingOverlapping = false;
    private Boolean emailCheckingOverlapping = false;
    private Boolean groupCheckingOverlapping = false;
    private ArrayList<Integer> group = new ArrayList<>();
    private int i,j;
    private Users existingUser;
    private UsersEntityRepository usersEntityRepository;

    @Autowired
    private void setUsersEntityRepository(UsersEntityRepository usersEntityRepository){
        this.usersEntityRepository = usersEntityRepository;
    };

    @Override
    public Boolean checkIfExistingIdOverlap(Users user){
        existingUser = usersEntityRepository.findIdByExistingId(user.getId().getUserId());
        if(existingUser.getId().getUserId().equals(user.getId().getUserId()))
            idCheckingOverlapping = true;
        return idCheckingOverlapping;
    }

    @Override
    public Boolean checkIfExistingPhoneOverlap(Users user){
        existingUser = usersEntityRepository.findPhoneByExistingPhone(user.getPhone());
        if(existingUser.getPhone().equals(user.getPhone()))
            phoneCheckingOverlapping = true;
        return phoneCheckingOverlapping;

    }

    @Override
    public Boolean checkIfExistingEmailOverlap(Users user){
        existingUser = usersEntityRepository.findEmailByExistingEmail(user.getEmail());
        if(existingUser.getEmail().equals(user.getEmail()))
            emailCheckingOverlapping = true;
        return emailCheckingOverlapping;
    }

    @Override
    public Boolean checkOverlappingGroup(Users user){
        group.add(user.getGroup1());
        group.add(user.getGroup2());
        group.add(user.getGroup3());
        group.add(user.getGroup4());

        for(i = 0; i < group.size(); i++){
            for(j = 0; j < group.size(); j++){
                if(group.get(i) == group.get(j))
                    groupCheckingOverlapping = true;
            }
        }
        return groupCheckingOverlapping;
    }

}
