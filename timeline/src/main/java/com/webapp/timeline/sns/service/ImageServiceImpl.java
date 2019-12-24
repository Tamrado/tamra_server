package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.exception.UnauthorizedUserException;
import com.webapp.timeline.exception.WrongCodeException;
import com.webapp.timeline.membership.service.interfaces.UserSignService;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Images;
import com.webapp.timeline.sns.dto.ImageDto;
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

import static com.webapp.timeline.sns.common.CommonTypeProvider.DELETED_EVENT_CHECK;


@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);
    private ImageS3Uploader imageUploader;
    private ImagesRepository imagesRepository;
    private UserSignService userSignService;
    private ServiceAspectFactory<Images> factory;
    private final int THUMBNAIL_HEIGHT = 290;
    private final int THUMBNAIL_WIDTH = 290;
    private final String IMAGE_FORMAT = "png";
    private final String TEMP_FILEPATH = "src/main/resources/thumbnail.png";

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

    @Override
    public ImageDto uploadImage(MultipartFile multipartFile, HttpServletRequest request) {
        logger.info("[ImageService] Upload Original Image.");

        String email = this.userSignService.extractUserFromToken(request)
                                        .getEmail();
        String url = "";
        File originalFile = null;

        try {
            url = this.imageUploader.upload(multipartFile, email);
            originalFile = this.imageUploader.getOriginalFile();

            if(! url.equals("")) {
                return ImageDto.builder()
                                .original(url)
                                .thumbnail(makeThumbNail(originalFile, email))
                                .build();
            }
        }
        catch(IOException aws_IO_exception) {
            throw new NoStoringException();
        }

        return null;
    }

    @Override
    public void saveImage(Images entity) {
        logger.info("[ImageService] Save image to database.");

        imagesRepository.save(entity);
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
                thumbnailUrl = uploadThumbnail(thumbnail, email);
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
