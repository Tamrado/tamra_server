package com.webapp.timeline.sns.web;

import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.exception.WrongCodeException;
import com.webapp.timeline.sns.service.ImageServiceImpl;
import com.webapp.timeline.sns.service.interfaces.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"3. Post Image"})
@RestController
@RequestMapping(value = "/post")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private ImageService imageService;

    @Autowired
    public void setImageService(ImageServiceImpl imageService) {
        this.imageService = imageService;
    }

    @ApiOperation(value = "무슨 일이 있으셨나요? - 사진 업로드 (request : postId, Image)",
                notes = "response : 201 -> 성공" +
                                "| 409 -> AWS S3에서 오류" +
                                "| 422 -> 썸네일 만들기 실패")
    @PostMapping(value = "upload/image")
    public ResponseEntity upload(@RequestParam(value = "postId") int postId,
                                 MultipartFile file,
                                 @ApiIgnore HttpServletRequest request) {

        logger.info("[ImageController] upload image.");

        try {
            this.imageService.uploadImage(postId, file, request);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(NoStoringException original_aws_exception) {
            logger.error("[ImageController] AWS S3 IOException while upload original multipartfile.");

            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        catch(WrongCodeException thumbnail_exception) {
            logger.error("[ImageController] Can not make thumbnail.");

            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
