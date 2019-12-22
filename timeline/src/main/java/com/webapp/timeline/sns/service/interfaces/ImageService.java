package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface ImageService {
    ImageDto uploadImage(MultipartFile file, HttpServletRequest request);
}
