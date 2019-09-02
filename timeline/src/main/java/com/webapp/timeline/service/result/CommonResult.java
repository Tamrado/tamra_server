package com.webapp.timeline.service.result;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;

public class CommonResult {

    @ApiModelProperty(value = "응답 성공여부 : true/false")
    private boolean success;

    @ApiModelProperty(value = "응답 코드 번호 : >= 0 정상, < 0 비정상")
    private int code;

    @ApiModelProperty(value = "응답 메시지")
    private String msg;

    public CommonResult(){
<<<<<<< HEAD:timeline/src/main/java/com/webapp/timeline/service/result/CommonResult.java
        success = false;
        code = -1;
        msg = null;
=======

>>>>>>> 1bb85d954bff476da70b1b312038057e1a9640aa:timeline/src/main/java/com/webapp/timeline/service/membership/CommonResult.java
    }
    public void setSuccess(boolean success){
        this.success = success;
    }
    public void setCode(int code){
        this.code = code;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }
    public boolean getSuccess(){
        return success;
    }
    public int getCode(){
        return code;
    }
    public String getMsg(){
        return msg;
    }
}
