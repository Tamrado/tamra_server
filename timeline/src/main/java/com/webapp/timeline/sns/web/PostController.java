package com.webapp.timeline.sns.web;

import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.service.PostService;
import com.webapp.timeline.sns.service.exception.UnauthorizedUserException;
import io.swagger.annotations.Api;
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

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;


@Api(tags = {"3. Post"})
@RestController
@RequestMapping(value="/post")
public class PostController {

    private PostService postServiceImpl;
    private UserSignServiceImpl userSignServiceImpl;
    private HttpHeaders header;
    private BindingErrorsPackage bindingErrorsPackage;
    private final static Logger logger = LoggerFactory.getLogger(PostController.class);


    @Autowired
    public void setPostService(PostService postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }

    @Autowired
    public void setUserSignService(UserSignServiceImpl userSignServiceImpl) {
        this.userSignServiceImpl = userSignServiceImpl;
    }


    @ApiOperation(value="글쓰기", notes="새 글 쓰기")
    @PostMapping(value="/upload", consumes={MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Posts> create(@Valid @RequestBody Posts post,
                                        @ApiIgnore HttpServletRequest httpServletRequest,
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

        post.setUserId(userSignServiceImpl.extractUserFromToken(httpServletRequest).getId());
        post = postServiceImpl.createPost(post);

        return new ResponseEntity<Posts>(post, header, HttpStatus.CREATED);
    }

    @ApiOperation(value="글 삭제", notes="자신이 쓴 글이 맞다면 글 삭제")
    @DeleteMapping(value="/delete/{postId}")
    public ResponseEntity delete(@PathVariable("postId") long postId,
                                        @ApiIgnore HttpServletRequest httpServletRequest) {

        String userId = "";
        BindingError bindingCustomError;
        BindingErrorsPackage bindingErrorsPackage = new BindingErrorsPackage();
        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            userId = this.userSignServiceImpl.extractUserFromToken(httpServletRequest).getId();
            return new ResponseEntity<Posts>(this.postServiceImpl.deletePost(postId, userId), header, HttpStatus.OK);
        }
        catch(EntityNotFoundException e1) {
            bindingCustomError = new BindingError
                                    .BindingErrorBuilder()
                                    .fieldName("postId")
                                    .fieldValue(String.valueOf(postId))
                                    .message("ENTITY NOT FOUND EXCEPTION")
                                    .code(String.valueOf(404))
                                    .build();

            bindingErrorsPackage.createCustomErrorDetail(bindingCustomError);
            header.add("notfound-error", bindingErrorsPackage.toJson());
            return new ResponseEntity<>(Collections.singletonMap("error", "NOTFOUND ERROR"), header, HttpStatus.NOT_FOUND);
        }
        catch(UnauthorizedUserException e2) {
            bindingCustomError = new BindingError
                                    .BindingErrorBuilder()
                                    .fieldName("userId")
                                    .fieldValue(userId)
                                    .message("USER NOT AUTHORIZED EXCEPTION")
                                    .code(String.valueOf(401))
                                    .build();

            bindingErrorsPackage.createCustomErrorDetail(bindingCustomError);
            header.add("unauthorized-error", bindingErrorsPackage.toJson());
            return new ResponseEntity<>(Collections.singletonMap("error", "UNAUTHORIZED ERROR"), header, HttpStatus.UNAUTHORIZED);
        }
        catch(Exception e3) {
            return new ResponseEntity<>(Collections.singletonMap("error", "INTERNAL SERVER ERROR"), header, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

