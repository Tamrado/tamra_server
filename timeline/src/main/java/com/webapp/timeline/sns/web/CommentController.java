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
                notes = "response : 200 -> 성공 " +
                                "| 400 -> 댓글 내용이 없을 때 " +
                                "| 404 -> 해당 글이 이미 지워졌을 때/ 없을 때 " +
                                "| 411 -> 댓글 내용 글자수 300자 초과 시")
    @PostMapping(value = "/{postId}/comment/register")
    public ResponseEntity register(@PathVariable("postId") long postId,
                                   @RequestBody Comments comment,
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
        catch(NoInformationException deleted_post) {
            logger.error("[CommentController] The Post already deleted : " + postId);

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(NoMatchPointException over_300_characters) {
            logger.error("[CommentController] Comment can NOT save over 300-character content.");

            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }
    }

    @ApiOperation(value = "댓글 삭제하기 (request : 글 Id)",
                notes = "response : 200 -> 삭제 성공 " +
                                "| 401 -> 로그인된 Id와 댓글 쓴 사람 Id가 다를 때" +
                                "| 404 -> 해당 글이 이미 지워졌을 때/ 없을 때 or 저장되지 않은 commentId " +
                                "| 422 -> 삭제가 반영되지 않을 때 (아직 댓글 남아있음)" +
                                "| 500 -> 트랜젝션 오류(front에서는 삭제되지 않았다고 사용자에게 공지)")
    @PutMapping(value = "/comment/{commentId}/remove")
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
            logger.error("[CommentController] The Post already deleted or Comment is not saved.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(UnauthorizedUserException unauthorized_user) {
            logger.error("[CommentController] This user is NOT authorized to delete.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(WrongCodeException no_affected_row) {
            logger.error("[CommentController] There is 0 affected row.");

            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch(InternalServerException internal_server_error) {
            logger.error("[CommentController] Transaction error/ Internal server error.");

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "댓글 수정하기 (request : 글 Id, 댓글 내용)",
                notes = "response : 200 -> 수정 (내용/ 시간) 성공 " +
                                "| 400 -> 수정이 불가능한 댓글 (이미 삭제됐는데 db 반영 안돼서 남아있을 경우)" +
                                "| 401 -> 로그인된 Id와 댓글 쓴 사람 Id가 다를 때" +
                                "| 404 -> 해당 글이 이미 지워졌을 때/ 없을 때 or 저장되지 않은 commentId " +
                                "| 422 -> 수정이 반영되지 않을 때" +
                                "| 500 -> 트랜젝션 오류(front에서는 수정되지 않았다고 사용자에게 공지)")
    @PutMapping(value = "/comment/{commentId}/edit")
    public ResponseEntity edit(@PathVariable("commentId") long commentId,
                               @RequestBody Comments comment,
                               @ApiIgnore HttpServletRequest request) {

        logger.info("[CommentController] Edit comment.");

        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>
                    (this.commentService.editComment(commentId, comment, request), header, HttpStatus.OK);
        }
        catch(BadRequestException already_deleted_comment) {
            logger.error("[CommentController] This is already DELETED comment. Can not edit it.");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch(NoInformationException no_comment_by_postId) {
            logger.error("[CommentController] The Post already deleted or Comment is not saved.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(UnauthorizedUserException unauthorized_user) {
            logger.error("[CommentController] This user is NOT authorized to edit comment.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(WrongCodeException no_affected_row) {
            logger.error("[CommentController] There is 0 affected row.");

            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch(InternalServerException internal_server_error) {
            logger.error("[CommentController] Transaction error/ Internal server error.");

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
