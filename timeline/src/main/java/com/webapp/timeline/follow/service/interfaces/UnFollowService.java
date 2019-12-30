package com.webapp.timeline.follow.service.interfaces;

import javax.servlet.http.HttpServletRequest;

public interface UnFollowService {
    void sendUnFollow(String fid, HttpServletRequest request) throws RuntimeException;
}
