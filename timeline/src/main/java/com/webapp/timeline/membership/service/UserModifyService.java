package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.result.LoggedInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserModifyService {
    void modifyUser(Users user) throws RuntimeException;
    LoggedInfo modifyImage(HttpServletRequest req, MultipartFile file)throws RuntimeException;
    void modifyIdentify(HttpServletRequest request, HttpServletResponse response)throws RuntimeException;
}
