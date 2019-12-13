package com.webapp.timeline.follow.service;

import com.webapp.timeline.membership.service.response.LoggedInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public interface FriendService {
    void matchNewRelationship(String uid,String fid) throws RuntimeException;
    ArrayList<LoggedInfo> sendFriendApplyList(HttpServletRequest request) throws RuntimeException;
    void invalidateFriendApplyAlarm(HttpServletRequest request,String fid) throws RuntimeException;
    ArrayList<LoggedInfo> sendFriendList(HttpServletRequest request) throws RuntimeException;
}
