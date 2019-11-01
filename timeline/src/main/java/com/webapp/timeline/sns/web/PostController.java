package com.webapp.timeline.sns.web;

import com.webapp.timeline.membership.service.UserSignService;
import com.webapp.timeline.sns.domain.Posts;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Api(tags = {"2. Post"})
@RestController
@RequestMapping(value="/post")
public class PostController {

    private PostService postServiceImpl;
    private UserSignService userSignService;
    private HttpHeaders header;
    private BindingErrorsPackage bindingErrorsPackage;
    private final static Logger logger = LoggerFactory.getLogger("com.webapp.timeline.web.PostController");


    @Autowired
    public void setPostService(PostService postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }

    @Autowired
    public void setUserSignService(UserSignService userSignService) {
        this.userSignService = userSignService;
    }


    @ApiOperation(value="글쓰기", notes="새 글 쓰기")
    @PostMapping(value="/upload", consumes={MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Posts> create(@Valid @RequestBody Posts post,
                                        @ApiIgnore HttpServletRequest httpServletRequest, HttpServletResponse response,
                                        @ApiIgnore BindingResult bindingResult) {

        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
        bindingErrorsPackage = new BindingErrorsPackage();

        if(post == null) {
                logger.error("[Null] Cannot upload POSTS with empty object.");
            return new ResponseEntity<>(null, header, HttpStatus.BAD_REQUEST);
        }

        if(bindingResult.hasErrors()) {
            bindingErrorsPackage.createErrorDetail(bindingResult);
            header.add("errors", bindingErrorsPackage.toJson());
        }

        post.setUserId(userSignService.extractUserFromToken(httpServletRequest,response).getId());
        postServiceImpl.createPost(post);

        return new ResponseEntity<Posts>(post, header, HttpStatus.CREATED);
    }

}

