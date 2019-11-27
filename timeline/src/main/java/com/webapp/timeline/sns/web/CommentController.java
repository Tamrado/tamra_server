package com.webapp.timeline.sns.web;


import com.webapp.timeline.sns.service.CommentServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"4. Post-Comment"})
@RestController
@RequestMapping(value = "/post")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private static final int MAXIMUM_CONTENT_LENGTH = 300;
    private CommentServiceImpl commentService;
    private HttpHeaders header;

    @Autowired
    public void setCommentServiceImpl(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @ApiOperation(value = "댓글쓰기 (request : 글 Id, 댓글 내)",
                notes = "response : 200 -> 성공 | 400 -> 댓글 내용이 없을 때 | 411 -> 댓글 내용 글자수 300자 초과 시")
    @PostMapping(value = "/{postId}/comment")
    public ResponseEntity register(@PathVariable("postId") long postId,
                                   String content,
                                   @ApiIgnore HttpServletRequest request) {

        logger.info("[CommentController] Register comment.");

        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        if(content == null || content.length() == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        else if(content.length() > MAXIMUM_CONTENT_LENGTH) {
            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }

        return new ResponseEntity<>
                (this.commentService.registerComment(postId, content, request), header, HttpStatus.OK);
    }
}
