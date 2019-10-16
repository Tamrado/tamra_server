package com.webapp.timeline.sns.web;


import com.webapp.timeline.sns.domain.PhotoVO;
import com.webapp.timeline.sns.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collections;
import java.util.List;

@Api(tags = {"3. Post: Files Upload"})
@RestController
@RequestMapping(value="/post")
public class PostFileUploadController {

    private Logger logger = LoggerFactory.getLogger(PostFileUploadController.class);
    private PostService postServiceImpl;
    private HttpHeaders header;
    private BindingErrorsPackage bindingErrorsPackage;

    PostFileUploadController() {}

    @Autowired
    public PostFileUploadController(PostService postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }


    @ApiOperation(value="post 이미지 업로드", notes="최대 10개까지 업로드 가능")
    @PostMapping(value="/upload/images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    public ResponseEntity filesUpload(MultipartFile[] files,
                                      @ApiIgnore BindingResult bindingResult) {

        header = new HttpHeaders();
        bindingErrorsPackage = new BindingErrorsPackage();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        if(files.length == 0) {
            return new ResponseEntity<>((List<PhotoVO>) null, HttpStatus.OK);
        }

        if(bindingResult.hasErrors()) {
            bindingErrorsPackage.createErrorDetail(bindingResult);
            header.add("errors", bindingErrorsPackage.toJson());
        }

        try {
            //return new ResponseEntity<>(postServiceImpl.uploadImages(files), header, HttpStatus.CREATED);
            return null;

        } catch(Exception e) {
            logger.error("[PostFileUploadController] ERROR : " + String.valueOf(e.getCause()));
            return new ResponseEntity<>(Collections.singletonMap("error", "INTERNAL SERVER ERROR"), header, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
