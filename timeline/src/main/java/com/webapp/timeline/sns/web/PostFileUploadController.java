package com.webapp.timeline.sns.web;


import com.webapp.timeline.sns.service.interfaces.PostService;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;


@Api(tags = {"3-1. Post: Files Upload"})
@RestController
@RequestMapping(value="/post")
public class PostFileUploadController {

    private Logger logger = LoggerFactory.getLogger(PostFileUploadController.class);
    private PostService postServiceImpl;
    private HttpHeaders header;

    PostFileUploadController() {}

    @Autowired
    public PostFileUploadController(PostService postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }

    @ApiOperation(value="post 이미지 업로드")
    @PostMapping(value="/upload/images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity filesUpload(@ApiIgnore HttpServletRequest httpServletRequest,
                                      @RequestParam("file") MultipartFile file) {

        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>(postServiceImpl.uploadImages(file, httpServletRequest), header, HttpStatus.CREATED);

        } catch(Exception e) {
            logger.error("[PostFileUploadController] ERROR : " + e.getCause());
            return new ResponseEntity<>(Collections.singletonMap("error", "INTERNAL SERVER ERROR"), header, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
