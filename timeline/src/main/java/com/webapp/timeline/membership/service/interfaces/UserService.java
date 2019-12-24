package com.webapp.timeline.membership.service.interfaces;

import com.webapp.timeline.membership.service.response.LoggedInfo;

import java.util.ArrayList;

public interface UserService {
    void saveImageURL(String userId,String url) throws RuntimeException;
    LoggedInfo setLoggedInfo(String userId) throws RuntimeException;
    ArrayList<String> sendActualUserFromList(ArrayList<String> userList) throws RuntimeException;
    void isTrueActualUser(String user) throws RuntimeException;
}
