package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.exception.WrongCodeException;
import com.webapp.timeline.membership.service.UserSignService;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Images;
import com.webapp.timeline.sns.repository.ImagesRepository;
import com.webapp.timeline.sns.service.interfaces.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);
    private ImageS3Uploader imageUploader;
    private ImagesRepository imagesRepository;
    private UserSignService userSignService;
    private final int THUMBNAIL_HEIGHT = 300;
    private final int THUMBNAIL_WIDTH = 300;
    private final String IMAGE_FORMAT = "png";
    private final String TEMP_FILEPATH = "src/main/resources/thumbnail.png";

    @Autowired
    public void setImageUploader(ImageS3Uploader imageUploader) {
        this.imageUploader = imageUploader;
    }

    @Autowired
    public void setImagesRepository(ImagesRepository imagesRepository) {
        this.imagesRepository = imagesRepository;
    }

    @Autowired
    public void setUserSignService(UserSignServiceImpl userSignService) {
        this.userSignService = userSignService;
    }

    @Override
    public void uploadImage(int postId, MultipartFile multipartFile, HttpServletRequest request) {
        logger.info("[ImageService] Upload Original Image.");

        String email = this.userSignService.extractUserFromToken(request)
                                        .getEmail();
        String url = "";
        File originalFile = null;

        try {
            url = this.imageUploader.upload(multipartFile, email);
            originalFile = this.imageUploader.getOriginalFile();
        }
        catch(IOException aws_IO_exception) {
            throw new NoStoringException();
        }

        if(! url.equals("")) {
            Images image = Images.builder()
                                .postId(postId)
                                .thumbnail(makeThumbNail(originalFile, email))
                                .url(url)
                                .build();

            this.imagesRepository.saveAndFlush(image);
        }
    }

    private String makeThumbNail(File original, String email) {
        logger.info("[ImageService] Try to make thumbnail..");

        File thumbnail = new File(TEMP_FILEPATH);
        String thumbnailUrl = "";

        try {
            BufferedImage originalBuffer = ImageIO.read(original);
            BufferedImage thumbnailBuffer = new BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);

            Graphics2D graphic = thumbnailBuffer.createGraphics();
            graphic.drawImage(originalBuffer, 0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null);

            if(ImageIO.write(thumbnailBuffer, IMAGE_FORMAT, thumbnail)) {
                thumbnailUrl =  uploadThumbnail(thumbnail, email);
                this.imageUploader.removeFileInLocal(original);
                this.imageUploader.removeFileInLocal(thumbnail);

                return thumbnailUrl;
            }
            else{
                throw new WrongCodeException();
            }
        }
        catch(IOException thumbnail_exception) {
            throw new WrongCodeException();
        }
    }

    private String uploadThumbnail(File thumbnail, String email) {
        return this.imageUploader.upload(thumbnail, email);
    }
}
