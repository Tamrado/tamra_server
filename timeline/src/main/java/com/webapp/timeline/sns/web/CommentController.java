package com.webapp.timeline.sns.web;


import com.webapp.timeline.exception.*;
import com.webapp.timeline.sns.domain.Comments;
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
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"4. Post-Comment"})
@RestController
@RequestMapping(value = "/post")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private CommentServiceImpl commentService;
    private HttpHeaders header;

    @Autowired
    public void setCommentServiceImpl(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @ApiOperation(value = "댓글쓰기 (request : 글 Id, 댓글 내용)",
                notes = "response : 200 -> 성공 | 400 -> 댓글 내용이 없을 때 | 411 -> 댓글 내용 글자수 300자 초과 시")
    @PostMapping(value = "/{postId}/comment/register")
    public ResponseEntity register(@PathVariable("postId") long postId,
                                   Comments comment,
                                   @ApiIgnore HttpServletRequest request) {

        logger.info("[CommentController] Register comment.");

        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>
                    (this.commentService.registerComment(postId, comment, request), header, HttpStatus.OK);
        }
        catch(BadRequestException no_content) {
            logger.error("[CommentController] There is NO CONTENT in this comment.");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch(NoMatchPointException over_300_characters) {
            logger.error("[CommentController] Comment can NOT save over 300-character content.");

            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }
    }

    @ApiOperation(value = "댓글 삭제하기 (request : 글 Id)",
                notes = "response : 200 -> 삭제 성공 " +
                                "| 401 -> 로그인된 Id와 댓글 쓴 사람 Id가 다를 때" +
                                "| 404 -> 들어온 commentId에 해당하는 comment가 없을 때 " +
                                "| 422 -> 삭제가 반영되지 않을 때 (아직 댓글 남아있음)" +
                                "| 500 -> 트랜젝션 오류(front에서는 삭제되지 않았다고 사용자에게 공지)")
    @DeleteMapping(value = "/comment/{commentId}/remove")
    public ResponseEntity remove(@PathVariable("commentId") long commentId,
                                 @ApiIgnore HttpServletRequest request) {

        logger.info("[CommentController] Remove comment.");

        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>
                    (this.commentService.removeComment(commentId, request), header, HttpStatus.OK);
        }
        catch(NoInformationException no_comment_by_postId) {
            logger.error("[CommentController] There is NO COMMENT found by the commentId : " + commentId);

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(UnauthorizedUserException unauthorized_user) {
            logger.error("[CommentController] This user is NOT authorized to delete.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        catch(InternalServerException internal_server_error) {
            logger.error("[CommentController] Transaction error/ Internal server error.");

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
