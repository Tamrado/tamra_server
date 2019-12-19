package com.webapp.timeline.sns.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface ImageService {
    void uploadImage(int postId, MultipartFile file, HttpServletRequest request);
}
