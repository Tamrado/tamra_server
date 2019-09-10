package com.webapp.timeline.service.result;

import io.swagger.annotations.ApiModelProperty;

public class CommonResult {

    @ApiModelProperty(value = "응답 성공여부 : true/false")
    private boolean success;

    @ApiModelProperty(value = "응답 코드 번호 : >= 0 정상, < 0 비정상")
    private int code;

    @ApiModelProperty(value = "응답 메시지")
    private String msg;

    public CommonResult(){
        success = false;
        code = 400;
        msg = null;
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
