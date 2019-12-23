package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.domain.Images;
import com.webapp.timeline.sns.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface ImageService {
    ImageDto uploadImage(MultipartFile file, HttpServletRequest request);

    void saveImage(Images entity);

    void deleteImage(long id, HttpServletRequest request);

    int deleteImageByPostId(int postId);


}
