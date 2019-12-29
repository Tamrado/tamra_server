package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.domain.Images;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface ImageService {
    void uploadImage(int postId, MultipartFile file, HttpServletRequest request);

    void saveImage(Images entity);

    void deleteImage(long id, HttpServletRequest request);

    int deleteImageByPostId(int postId);


}
