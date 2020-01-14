package com.webapp.timeline.membership.service.interfaces;

import com.webapp.timeline.membership.domain.Profiles;
import com.webapp.timeline.membership.service.response.LoggedInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public interface UserService {
    LoggedInfo setLoggedInfo(String userId) throws RuntimeException;
    ArrayList<String> sendActualUserFromList(ArrayList<String> userList) throws RuntimeException;
    void isTrueActualUser(String user) throws RuntimeException;
    void saveImageURL(Profiles profile) throws RuntimeException;
    void isThereAnyProfileId(String userId) throws RuntimeException;
    String sendTokenCategory(String userId);
}
