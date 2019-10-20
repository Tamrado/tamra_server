package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.result.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidateService {
    private SignUpValidator signUpValidator;
    public ValidateService(){
    }
    @Autowired
    public ValidateService(SignUpValidator signUpValidator){
        this.signUpValidator = signUpValidator;
    }

    public CommonResult checkId(String id){
        CommonResult commonResult = new CommonResult();
        if(!signUpValidator.checkIdExists(id))
            commonResult.setSuccessResult(200,"");
        return commonResult;
    }
    public CommonResult checkEmail(String email){
        CommonResult commonResult = new CommonResult();
        if(!signUpValidator.checkEmailExists(email))
            commonResult.setSuccessResult(200,"");
        return commonResult;
    }
    public CommonResult checkPhone(String phone){
        CommonResult commonResult = new CommonResult();
        if(!signUpValidator.checkPhoneExists(phone))
            commonResult.setSuccessResult(200,"");
        return commonResult;
    }

}
