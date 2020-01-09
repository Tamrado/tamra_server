package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.domain.Images;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

public interface ImageService {
    Images uploadImage(int postId, MultipartFile file, HttpServletRequest request);

    Images saveImage(Images entity);

    void deleteImage(long id, HttpServletRequest request);

    int deleteImageByPostId(int postId);


}
