package com.webapp.timeline.membership.service.result;


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
