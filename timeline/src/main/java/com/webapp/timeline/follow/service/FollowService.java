package com.webapp.timeline.follow.service;

import com.webapp.timeline.follow.service.response.MyInfo;

import javax.servlet.http.HttpServletRequest;

public interface FollowService {
    MyInfo sendUserInfo(HttpServletRequest request) throws RuntimeException;
    void sendFollow(String fid,HttpServletRequest httpServletRequest)throws RuntimeException;
}
