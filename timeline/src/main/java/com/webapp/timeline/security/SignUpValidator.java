package com.webapp.timeline.security;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.util.ArrayList;

@Component
public class SignUpValidator implements Validator {

    private String idfield = false;
    private Boolean phoneCheckingOverlapping = false;
    private Boolean emailCheckingOverlapping = false;
    private Boolean groupCheckingOverlapping = false;
    private ArrayList<Integer> group = new ArrayList<>();
    private int i,j;
    private Users users;
    private UsersEntityRepository usersEntityRepository;
    private UserDetails userDetails;
    @Override
    public boolean supports(Class<?> arg0){
        return Users.class.isAssignableFrom(arg0);
    }
    @Override
    public void validate(Object obj, Errors errors){
        users = (Users) obj;
        if(checkIfIdOverlap(users))
            errors.rejectValue("userId","overlap");
        if(checkIfEmailOverlap(users))
            errors.rejectValue("email","overlap");
        if(checkIfPhoneOverlap(users))
            errors.rejectValue("phone","overlap");
        if(checkIfGroupOverlap(users)) {
            errors.rejectValue("group", "overlap");
        }
    }
    public String checkIfIdOverlap(Users user){
        users = usersEntityRepository.findIdByExistingId(user.getId());
        if(users.getId().equals(user.getId()))
            idCheckingOverlapping = ;
        return idCheckingOverlapping;
    }

    public String checkIfPhoneOverlap(Users user){
        users = usersEntityRepository.findPhoneByExistingPhone(user.getPhone());
        if(users.getPhone().equals(user.getPhone()))
            phoneCheckingOverlapping = true;
        return phoneCheckingOverlapping;

    }

    public String checkIfEmailOverlap(Users user){
        users = usersEntityRepository.findEmailByExistingEmail(user.getEmail());
        if(users.getEmail().equals(user.getEmail()))
            emailCheckingOverlapping = true;
        return emailCheckingOverlapping;
    }

    public String checkIfGroupOverlap(Users user){
        group.add(user.getGroup1());
        group.add(user.getGroup2());
        group.add(user.getGroup3());
        group.add(user.getGroup4());

        for(i = 0; i < group.size(); i++){
            for(j = 0; j < group.size(); j++){
                if(group.get(i) == group.get(j)) {
                    groupCheckingOverlapping = true;
                    break;
                }

            }
        }
        return groupCheckingOverlapping;
    }


}
