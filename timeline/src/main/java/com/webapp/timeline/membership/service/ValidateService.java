package com.webapp.timeline.membership.service;

import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.membership.security.SignUpValidator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidateService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private SignUpValidator signUpValidator;
    public ValidateService(){
    }
    @Autowired
    public ValidateService(SignUpValidator signUpValidator){
        this.signUpValidator = signUpValidator;
    }

    public void checkId(String id) throws RuntimeException{
        if(signUpValidator.checkIdExists(id))
            throw new NoMatchPointException();
    }
    public void checkEmail(String email,String id) throws RuntimeException{
        if ((id != "null" && signUpValidator.checkEmailExists(false, email, id)) || (id == "null" && signUpValidator.checkEmailExists(true, email, null)))
           throw new NoMatchPointException();
    }
    public void checkPhone(String phone,String id) throws RuntimeException{
        if((id != "null" && signUpValidator.checkPhoneExists(false, phone, id)) || (id == "null" && signUpValidator.checkPhoneExists(true, phone, null)))
            throw new NoMatchPointException();
    }
}
