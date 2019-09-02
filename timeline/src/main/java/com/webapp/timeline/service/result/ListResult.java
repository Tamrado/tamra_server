package com.webapp.timeline.service.result;

import com.webapp.timeline.service.result.CommonResult;

import java.util.List;

public class ListResult<T> extends CommonResult {
    private List<T> list;
    public List<T> getList(){
        return list;
    }
    public void setList(List<T> list){
        this.list = list;
    }

}
