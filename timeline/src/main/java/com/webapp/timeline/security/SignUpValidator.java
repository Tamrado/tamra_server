package com.webapp.timeline.security;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import com.webapp.timeline.service.membership.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SignUpValidator{

    Logger log = LoggerFactory.getLogger(this.getClass());
    private Boolean nameCheckingOverlapping = false;
    private Boolean emailCheckingOverlapping = false;
    @Autowired
    private UsersEntityRepository usersEntityRepository;

    public CommonResult validate(Users users, CommonResult commonResult){
        commonResult.setCode(0);
        commonResult.setSuccess(true);
        commonResult.setMsg("success");

        if(checkIfEmailOverlap(users)) {
            commonResult.setCode(-1);
            commonResult.setSuccess(false);
            commonResult.setMsg("overlapped email");
        }
        if(checkIfEmailIsWrongForm(users)){
            commonResult.setCode(-1);
            commonResult.setSuccess(false);
            commonResult.setMsg("wrong formed email");
        }
        if(checkIfPasswordIsWrongForm(users)){
            commonResult.setCode(-1);
            commonResult.setSuccess(false);
            commonResult.setMsg("wrong formed password");
        }

        return commonResult;

    }
    public Boolean checkIfEmailIsWrongForm(Users user){
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(user.getEmail());
        if(m.matches())
            return false;

        else return true;
    }
    public Boolean checkIfPasswordIsWrongForm(Users user){
        Boolean returnValue = false;

        //정규식 (영문(대소문자 구분), 숫자, 특수문자 조합, 9~12자리)
        String regex = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{9,12}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(user.getPassword());
        if(!m.matches()) returnValue = true;
        log.error(returnValue.toString());
        //정규식 (같은 문자 4개 이상 사용 불가)
        regex =  "(.)\\1\\1\\1";
        p = Pattern.compile(regex);
        m = p.matcher(user.getPassword());
        if(m.find()) returnValue = true;
        log.error(returnValue.toString());
        return returnValue;
    }

    public Boolean checkIfEmailOverlap(Users user){
        Users result;
        try {
            result = usersEntityRepository.findEmailByExistingEmail(user.getEmail());
        } catch(NullPointerException e) {
            result = null;
        }
        Optional<Users> optional = Optional.ofNullable(result);
        if(optional.isPresent())
            emailCheckingOverlapping = true;
        return emailCheckingOverlapping;
    }

}
