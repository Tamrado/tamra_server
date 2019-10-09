package com.webapp.timeline.web;

import com.webapp.timeline.domain.Posts;
import com.webapp.timeline.service.membership.UserService;
import com.webapp.timeline.service.posting.PostService;
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
import javax.validation.Valid;


@Api(tags = {"2. Post"})
@RestController
@RequestMapping(value="/post")
public class PostController {

    private PostService postServiceImpl;
    private UserService userService;
    private HttpHeaders header;
    private BindingErrorsPackage bindingErrorsPackage;
    private final static Logger logger = LoggerFactory.getLogger("com.webapp.timeline.web.PostController");


    @Autowired
    public void setPostService(PostService postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @ApiOperation(value="글쓰기", notes="새 글 쓰기")
    @PostMapping(value="/upload", consumes={MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    public ResponseEntity<Posts> create(@Valid @RequestBody Posts post,
                                                @ApiIgnore BindingResult bindingResult) {

        header = new HttpHeaders();
        bindingErrorsPackage = new BindingErrorsPackage();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        if(post == null) {
                logger.error("[Null] Cannot upload POSTS with empty object.");
            return new ResponseEntity<>(null, header, HttpStatus.BAD_REQUEST);
        }

        post.setUserId(userService.extractUserFromToken().getId());
        postServiceImpl.createPost(post);

        return new ResponseEntity<Posts>(post, header, HttpStatus.CREATED);
    }

}

