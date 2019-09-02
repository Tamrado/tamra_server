package com.webapp.timeline.security;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import com.webapp.timeline.service.result.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Configurable
public class SignUpValidator{
    private UsersEntityRepository usersEntityRepository;
<<<<<<< HEAD
=======
    Logger log = LoggerFactory.getLogger(this.getClass());
>>>>>>> 1bb85d954bff476da70b1b312038057e1a9640aa
    private ArrayList<Integer> group = new ArrayList<>();

    @Autowired
    public SignUpValidator(UsersEntityRepository usersEntityRepository){
        this.usersEntityRepository = usersEntityRepository;
    }

    public CommonResult validate(Users users){
        CommonResult commonResult = new CommonResult();
        commonResult.setCode(0);
        commonResult.setSuccess(true);
        commonResult.setMsg("success");

        if(checkIfObjectOverlap(users)) {
            commonResult.setCode(-1);
            commonResult.setSuccess(false);
            commonResult.setMsg("overlapped id or phone or email");
        }
        else if(checkIfEmailIsWrongForm(users)){
            commonResult.setCode(-1);
            commonResult.setSuccess(false);
            commonResult.setMsg("wrong formed email");
        }
        else if(checkIfPasswordIsWrongForm(users)){
            commonResult.setCode(-1);
            commonResult.setSuccess(false);
            commonResult.setMsg("wrong formed password");
        }
        else if(checkIfGroupOverlap(users)){
            commonResult.setCode(-1);
            commonResult.setSuccess(false);
            commonResult.setMsg("overlapped group");
        }
        return commonResult;

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

<<<<<<< HEAD
    private Boolean checkIfObjectOverlap(Users user){
=======
    public Boolean checkIfObjectOverlap(Users user){
>>>>>>> 1bb85d954bff476da70b1b312038057e1a9640aa
        if(usersEntityRepository.findOverlappedObject(user.getId(),user.getEmail(),user.getPhone()).isEmpty())
            return false;
        return true;
    }

<<<<<<< HEAD
    private Boolean checkIfGroupOverlap(Users user){
=======
    public Boolean checkIfGroupOverlap(Users user){
>>>>>>> 1bb85d954bff476da70b1b312038057e1a9640aa
        group.add(user.getGroup1());
        group.add(user.getGroup2());
        group.add(user.getGroup3());
        group.add(user.getGroup4());

        for(int i = 0; i< group.size(); i++){
            for(int j = 0; j < group.size(); j++){
                if(i != j && group.get(i) == group.get(j))
                    return true;
            }
        }
        return false;
    }
}
