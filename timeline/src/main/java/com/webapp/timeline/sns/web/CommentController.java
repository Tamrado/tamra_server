package com.webapp.timeline.sns.web;


import com.webapp.timeline.exception.*;
import com.webapp.timeline.sns.domain.Comments;
import com.webapp.timeline.sns.model.CustomPageRequest;
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
                                "| 404 -> 해당 글이 이미 지워졌을 때/ 없을 때 " +
                                "| 409 -> 댓글 내용 0글자 이거나 글자수 300자 초과 시")
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
        catch(NoInformationException deleted_post) {
            logger.error("[CommentController] The Post already deleted : " + postId);

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(NoStoringException too_short_or_over_300_characters) {
            logger.error("[CommentController] Comment can NOT save empty or over 300-character content.");

            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @ApiOperation(value = "댓글 삭제하기 (request : 글 Id)",
                notes = "response : 200 -> 삭제 성공 " +
                                "| 401 -> 로그인된 Id와 댓글 쓴 사람 Id가 다를 때" +
                                "| 404 -> 해당 글이 이미 지워졌을 때/ 없을 때 or 저장되지 않은 commentId " +
                                "| 422 -> 삭제가 반영되지 않을 때 (아직 댓글 남아있음)")
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
    }

    @ApiOperation(value = "댓글 수정하기 (request : 글 Id, 댓글 내용)",
                notes = "response : 200 -> 수정 (내용/ 시간) 성공 " +
                                "| 400 -> 수정이 불가능한 댓글 (이미 삭제됐는데 db 반영 안돼서 남아있을 경우)" +
                                "| 401 -> 로그인된 Id와 댓글 쓴 사람 Id가 다를 때" +
                                "| 404 -> 해당 글이 이미 지워졌을 때/ 없을 때 or 저장되지 않은 commentId " +
                                "| 409 -> 수정한 댓글 내용이 0글자 or 300글자 초과일 때" +
                                "| 422 -> 수정이 반영되지 않을 때")
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
        catch(NoStoringException empty_or_too_long_content) {
            logger.error("[CommentController] There are 0 or over 300 characters in content.");

            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        catch(WrongCodeException no_affected_row) {
            logger.error("[CommentController] There is 0 affected row.");

            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ApiOperation(value = "게시글 내의 댓글 목록 보기 (request : 글 Id, paging 정보 (ASC로 요청하기))",
                notes = "response : 200 -> 목록 보기 성공 " +
                                "| 404 -> 해당 글이 이미 지워졌을 때 / 없을 때")
    @GetMapping(value = "/{postId}/comment/list")
    public ResponseEntity list(@PathVariable("postId") long postId,
                               CustomPageRequest pageRequest) {

        logger.info("[CommentController] List comments by postId.");

        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>
                    (this.commentService.listAllCommentsByPostId(pageRequest.of("commentId"), postId), header, HttpStatus.OK);
        }
        catch(NoInformationException deleted_post) {
            logger.error("[CommentController] The Post already deleted : " + postId);

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
