package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.response.ValidationInfo;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

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

    public ValidationInfo checkId(String id, HttpServletResponse response){
        ValidationInfo validationInfo = new ValidationInfo();
        response.setStatus(200);
        if(signUpValidator.checkIdExists(id)) {
            validationInfo.setIssue("exist");
            response.setStatus(400);
            validationInfo.setObjectName("id");
        }
        return validationInfo;
    }
    public ValidationInfo checkEmail(String email,String id,HttpServletResponse response){
        ValidationInfo validationInfo = new ValidationInfo();
        response.setStatus(200);
        if ((id != "null" && signUpValidator.checkEmailExists(false, email, id)) || (id == "null" && signUpValidator.checkEmailExists(true, email, null))) {
            validationInfo.setIssue("exist");
            response.setStatus(400);
            validationInfo.setObjectName("email");
        }
        return validationInfo;
    }
    public ValidationInfo checkPhone(String phone,String id,HttpServletResponse response){
        ValidationInfo validationInfo = new ValidationInfo();
        response.setStatus(200);
        if((id != "null" && signUpValidator.checkPhoneExists(false, phone, id)) || (id == "null" && signUpValidator.checkPhoneExists(true, phone, null))) {
            validationInfo.setIssue("exist");
            response.setStatus(400);
            validationInfo.setObjectName("phone");
        }
        return validationInfo;
    }
}
