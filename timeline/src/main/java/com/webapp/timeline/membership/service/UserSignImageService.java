package com.webapp.timeline.membership.service;

import com.webapp.timeline.membership.service.response.LoggedInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserSignImageService {
    LoggedInfo uploadUserSignImage(MultipartFile file, String userId, HttpServletResponse response) throws IOException,RuntimeException;
    void userImageUpload(MultipartFile multipartFile, String userId) throws RuntimeException, IOException;
}
