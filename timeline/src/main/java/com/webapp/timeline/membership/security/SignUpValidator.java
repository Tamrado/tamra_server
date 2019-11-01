package com.webapp.timeline.membership.security;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.result.ValidationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SignUpValidator{
    private UsersEntityRepository usersEntityRepository;
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SignUpValidator(UsersEntityRepository usersEntityRepository){
        this.usersEntityRepository = usersEntityRepository;
    }
    public ValidationInfo validateForModify(Users users, HttpServletResponse response){
        ValidationInfo validationInfo = new ValidationInfo();
        response.setStatus(200);
        if(checkIfEmailIsWrongForm(users)){
            response.setStatus(400);

            validationInfo.setObjectName("email");
            validationInfo.setIssue("form");
            return validationInfo;
        }
        if(checkIfPhoneIsWrongForm(users)){
            response.setStatus(400);
            validationInfo.setIssue("form");
            validationInfo.setObjectName("phone");
            return validationInfo;
        }
        if(checkIfPasswordIsWrongForm(users)) {
            response.setStatus(400);
            validationInfo.setIssue("form");
            validationInfo.setObjectName("password");
            return validationInfo;
        }
        if(checkIfObjectModifyOverlap(users)) {
            response.setStatus(400);
            validationInfo.setIssue("exist");
            return validationInfo;
        }
        return validationInfo;
    }

    public ValidationInfo validate(Users users, HttpServletResponse response){
        ValidationInfo validationInfo = new ValidationInfo();
        response.setStatus(200);
        if(checkIfObjectOverlap(users)){
            response.setStatus(400);
            validationInfo.setIssue("exist");
            return validationInfo;
        }
        return validateForModify(users,response);

    }
    private Boolean checkIfPhoneIsWrongForm(Users user){
        String regex = "^01(?:0|1|[6-9])-(\\d{3}|\\d{4})-(\\d{4})$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(user.getPhone());
        if(m.matches())
            return false;
        else
            return true;
    }
    private Boolean checkIfEmailIsWrongForm(Users user){
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(user.getEmail());
        if(m.matches())
            return false;

        else return true;
    }
    private Boolean checkIfPasswordIsWrongForm(Users user){
        Boolean returnValue = false;

        //정규식 (영문(대소문자 구분), 숫자, 특수문자 조합, 9~12자리)
        String regex = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{9,12}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(user.getPassword());
        if(!m.matches()) returnValue = true;
        //정규식 (같은 문자 4개 이상 사용 불가)
        regex =  "(.)\\1\\1\\1";
        p = Pattern.compile(regex);
        m = p.matcher(user.getPassword());
        if(m.find()) returnValue = true;
        return returnValue;
    }
    private Boolean checkIfObjectModifyOverlap(Users user){
        Boolean overlappedChecking = false;
        log.info(user.getId());
        if(usersEntityRepository.findEmailByExistingEmail(user.getEmail()) != null && !usersEntityRepository.findEmailByExistingEmail(user.getEmail()).getId().equals(user.getId()) )
            overlappedChecking = true;
        else if(usersEntityRepository.findPhoneByExistingPhone(user.getPhone()) != null && !usersEntityRepository.findPhoneByExistingPhone(user.getPhone()).getId().equals(user.getId()))
            overlappedChecking = true;

        return overlappedChecking;
    }

    private Boolean checkIfObjectOverlap(Users user){
        if(usersEntityRepository.findOverlappedObject(user.getId(),user.getEmail(),user.getPhone()).isEmpty())
            return false;
        return true;
    }
    public Boolean checkIdExists(String id){
        if(usersEntityRepository.findIdByExistingId(id) != null)
            return true;
        return false;
    }
    public Boolean checkEmailExists(boolean mode,String email,String id){
        if (usersEntityRepository.findEmailByExistingEmail(email) != null &&(mode || (!mode && !usersEntityRepository.findEmailByExistingEmail(email).getId().equals(id))))
            return true;
        return false;

    }
    public Boolean checkPhoneExists(boolean mode,String phone,String id){
        if(usersEntityRepository.findPhoneByExistingPhone(phone)!= null &&(mode || (!mode && !usersEntityRepository.findPhoneByExistingPhone(phone).getId().equals(id))))
            return true;
        return false;
    }
}