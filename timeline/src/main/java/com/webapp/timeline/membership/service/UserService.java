package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.service.result.LoggedInfo;

public interface UserService {
    void saveImageURL(String userId,String url) throws RuntimeException;
    LoggedInfo setLoggedInfo(String userId) throws RuntimeException;
}
