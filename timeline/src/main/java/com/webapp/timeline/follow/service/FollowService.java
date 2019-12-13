package com.webapp.timeline.follow.service;

import com.webapp.timeline.follow.service.response.FollowInfo;
import com.webapp.timeline.follow.service.response.PostProfileInfo;


import javax.servlet.http.HttpServletRequest;

public interface FollowService {
    FollowInfo sendMyInfo(HttpServletRequest request) throws RuntimeException;
    PostProfileInfo sendFriendInfo(String fid) throws RuntimeException;
    void sendFollow(String fid,HttpServletRequest httpServletRequest)throws RuntimeException;
}
