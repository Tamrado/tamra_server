package com.webapp.timeline.sns.web;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Posts;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Api(tags = {"3. Post"})
@RestController
@RequestMapping(value="/post")
public class PostController {

    private final static Logger logger = LoggerFactory.getLogger(PostController.class);
    private PostService postServiceImpl;
    private HttpHeaders header;


    @Autowired
    public void setPostService(PostService postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }


    @ApiOperation(value = "글쓰기 (request : 글 내용, show-level)",
                notes="response : 200 -> 성공 " +
                                "| 400 -> 글 내용이 없을 때 " +
                                "| 411 -> 글 내용 글자수 255글자 초과 시")
    @PostMapping(value = "/upload", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity create(@Valid @RequestBody Posts post,
                                        @ApiIgnore HttpServletRequest request) {

        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>(this.postServiceImpl.createPost(post, request), header, HttpStatus.OK);
        }
        catch(BadRequestException no_content) {
            logger.error("[PostController] There is NO CONTENT in this post.");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch(NoMatchPointException over_255_characters) {
            logger.error("[PostController] Post can NOT save over 300-character content.");

            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }
    }
    
}

