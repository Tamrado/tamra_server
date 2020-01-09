package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.exception.UnauthorizedUserException;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.membership.service.interfaces.UserSignService;
import com.webapp.timeline.sns.domain.Images;
import com.webapp.timeline.sns.repository.ImagesRepository;
import com.webapp.timeline.sns.service.interfaces.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.concurrent.Callable;

import static com.webapp.timeline.sns.common.CommonTypeProvider.DELETED_EVENT_CHECK;
import static com.webapp.timeline.sns.common.CommonTypeProvider.NEW_EVENT_CHECK;


@Service
public class ImageServiceImpl implements ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);
    private ImageS3Uploader imageUploader;
    private ImagesRepository imagesRepository;
    private UserSignService userSignService;
    private ServiceAspectFactory<Images> factory;

    ImageServiceImpl() {
    }

    @Autowired
    public ImageServiceImpl(ImageS3Uploader imageUploader,
                            ImagesRepository imagesRepository,
                            UserSignServiceImpl userSignService,
                            ServiceAspectFactory<Images> factory) {
        this.imageUploader = imageUploader;
        this.imagesRepository = imagesRepository;
        this.userSignService = userSignService;
        this.factory = factory;
    }

    @Transactional
    @Override
    public Images uploadImage(int postId, MultipartFile multipartFile, HttpServletRequest request) {
        logger.info("[ImageService] Upload Original Image.");

        String email = this.userSignService.extractUserFromToken(request)
                                           .getEmail();
        String url = "";

        try {
            url = this.imageUploader.upload(multipartFile, email);

            if(! url.equals("")) {
               return saveImage(Images.builder()
                                .postId(postId)
                                .url(url)
                                .deleted(NEW_EVENT_CHECK)
                                .build());
            }
        }
        catch(IOException aws_IO_exception) {
            throw new NoStoringException();
        }
        return null;
    }

    @Override
    public Images saveImage(Images entity) {
        logger.info("[ImageService] Save image to database.");

        imagesRepository.save(entity);
        return entity;
    }

    @Override
    public void deleteImage(long id, HttpServletRequest request) {
        logger.info("[ImageService] Delete 1 image by id.");

        String userId = factory.extractLoggedIn(request);
        Images image = this.imagesRepository.findById(id)
                                            .orElseThrow(NoInformationException::new);
        String author = factory.checkDeleteAndGetIfExist(image.getPostId())
                                .getAuthor();

        if(author != null && author.equals(userId)) {
            image.setDeleted(DELETED_EVENT_CHECK);
            factory.takeActionByQuery(this.imagesRepository.markDeleteByImageId(image));
        }
        else if(author == null || author.equals("")) {
            throw new NoInformationException();
        }
        else {
            throw new UnauthorizedUserException();
        }
    }

    @Override
    public int deleteImageByPostId(int postId) {
        logger.info("[ImageService] Delete all-images by postId.");

        return this.imagesRepository.markDeleteByPostId(postId);
    }

}
