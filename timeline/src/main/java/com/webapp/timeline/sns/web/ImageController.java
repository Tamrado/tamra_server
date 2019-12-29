package com.webapp.timeline.sns.web;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.exception.UnauthorizedUserException;
import com.webapp.timeline.exception.WrongCodeException;
import com.webapp.timeline.sns.service.ImageServiceImpl;
import com.webapp.timeline.sns.service.interfaces.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"3. Post"})
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping(value = "/api/post")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private ImageService imageService;
    private HttpHeaders headers;

    ImageController() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Autowired
    public void setImageService(ImageServiceImpl imageService) {
        this.imageService = imageService;
    }

    @ApiOperation(value = "무슨 일이 있으셨나요? - 사진 업로드 (request : Image)",
                notes = "response : 201 -> 성공" +
                                "| 409 -> AWS S3에서 오류" +
                                "| 422 -> 썸네일 만들기 실패")
    @PostMapping(value = "upload/{postId}/image")
    public ResponseEntity upload(@PathVariable("postId") int postId,
                                 MultipartFile file,
                                 HttpServletRequest request) {

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

    @ApiOperation(value = "무슨 일이 있으셨나요? - 사진 개별 삭제 (request : image-id)",
                notes = "response : 200 -> 성공" +
                                "| 401 -> 로그인된 Id와 글/사진 올린 사람 Id 다를 때 or user 없을 경우 (권한 없음) " +
                                "| 404 -> 사진 or 글이 이미 삭제됨 ")
    @DeleteMapping(value = "image/{id}/delete")
    public ResponseEntity delete(@PathVariable("id") long id,
                                 HttpServletRequest request) {

        logger.info("[ImageController] delete image.");

        try {
            this.imageService.deleteImage(id, request);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(UnauthorizedUserException unauthorized_user) {
            logger.error("[ImageController] This user is NOT authorized to delete.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoInformationException no_image_or_post) {
            logger.error("[ImageController] The Post already deleted or Image is not saved.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
