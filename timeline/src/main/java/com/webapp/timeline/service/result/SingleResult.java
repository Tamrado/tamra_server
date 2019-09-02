package com.webapp.timeline.service.result;


import com.webapp.timeline.service.result.CommonResult;

public class SingleResult<T> extends CommonResult {
    private T data;
    public SingleResult(){
        super();
        data = null;
    }
    public T getData() {
        return data;
    }
    public void setData(T data){
        this.data = data;
    }
}