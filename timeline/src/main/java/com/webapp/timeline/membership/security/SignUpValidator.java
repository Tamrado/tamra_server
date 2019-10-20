package com.webapp.timeline.membership.security;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.result.CommonResult;
import com.webapp.timeline.membership.service.result.SingleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Configurable
public class SignUpValidator{
    private UsersEntityRepository usersEntityRepository;
    Logger log = LoggerFactory.getLogger(this.getClass());
    private ArrayList<Integer> group = new ArrayList<>();

    @Autowired
    public SignUpValidator(UsersEntityRepository usersEntityRepository){
        this.usersEntityRepository = usersEntityRepository;
    }
    public CommonResult validateForModify(Users users){
        CommonResult commonResult= new CommonResult();
        if(checkIfEmailIsWrongForm(users))
            commonResult.setMsg("wrong formed email");
        else if(checkIfPhoneIsWrongForm(users))
            commonResult.setMsg("wrong formed phone number");
        else if(checkIfPasswordIsWrongForm(users))
            commonResult.setMsg("wrong formed password");

        else if(checkIfObjectModifyOverlap(users))
            commonResult.setMsg("overlapped object");
        else{
            commonResult.setCode(200);
            commonResult.setSuccess(true);
            commonResult.setMsg("success");
        }
        return commonResult;
    }

    public CommonResult validate(Users users){
        CommonResult commonResult = new SingleResult<>();
        if(checkIfObjectOverlap(users))
            commonResult.setMsg("overlapped id or phone or email");
        else
            commonResult = validateForModify(users);
        return commonResult;

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
        if(usersEntityRepository.findEmailByExistingEmail(user.getEmail()) != null && usersEntityRepository.findEmailByExistingEmail(user.getEmail()).getId() != user.getId())
            overlappedChecking = true;
        else if(usersEntityRepository.findPhoneByExistingPhone(user.getPhone()) != null && usersEntityRepository.findPhoneByExistingPhone(user.getPhone()).getId() != user.getId())
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
    public Boolean checkEmailExists(String email){
        if(usersEntityRepository.findEmailByExistingEmail(email) != null)
            return true;
        return false;
    }
    public Boolean checkPhoneExists(String phone){
        if(usersEntityRepository.findPhoneByExistingPhone(phone)!= null)
            return true;
        return false;
    }
}
