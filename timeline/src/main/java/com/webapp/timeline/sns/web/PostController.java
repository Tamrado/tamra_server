package com.webapp.timeline.sns.web;

import com.webapp.timeline.exception.*;
import com.webapp.timeline.sns.dto.request.EventRequest;
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

import javax.servlet.http.HttpServletRequest;


@Api(tags = {"3. Post"})
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping(value = "/api/post")
public class PostController {

    private final static Logger logger = LoggerFactory.getLogger(PostController.class);
    private PostService postServiceImpl;
    private HttpHeaders header;

    @Autowired
    public void setPostService(PostService postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }


    @ApiOperation(value = "글쓰기 : 무슨 일이 있으셨나요? (request : 글 내용, show-level)",
                notes = "response : 201 -> 성공 " +
                                "| 401 -> user 없을 경우 (권한 없음) " +
                                "| 409 -> 글 내용 글자수가 0이거나 1000글자 초과 시 (사진 있을 경우 0 허용)")
    @PostMapping(value = "/upload", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity create(@RequestBody EventRequest eventRequest,
                                 HttpServletRequest request) {

        logger.info("[PostController] create post.");
        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>(this.postServiceImpl.createEvent(eventRequest, request), header, HttpStatus.CREATED);
        }
        catch(UnauthorizedUserException no_user) {
            logger.error("[PostController] No user.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoStoringException too_long_or_short_content) {
            logger.error("[PostController] The content is empty or too long.");

            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @ApiOperation(value = "글 삭제하기 (request : 글 Id)",
                notes = "response : 200 -> 성공 " +
                                "| 401 -> 로그인된 Id와 글 쓴 사람 Id가 다를 때 or user 없을 때 (권한 없음) " +
                                "| 404 -> 삭제됐거나 찾을 수 없는 post " )
    @DeleteMapping(value = "/{postId}/delete")
    public ResponseEntity delete(@PathVariable("postId") int postId,
                                 HttpServletRequest request) {

        logger.info("[PostController] delete post.");
        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>
                    (this.postServiceImpl.deletePost(postId, request), header, HttpStatus.OK);
        }
        catch(UnauthorizedUserException unauthorized_user) {
            logger.error("[PostController] This user is NOT authorized to delete.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoInformationException no_post) {
            logger.error("[PostController] CanNOT find (or already deleted) post by post-id.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "글 수정하기 (request : 글 Id, 글 내용/ show-level)",
                notes = "response : 200 -> 성공 " +
                                "| 304 -> 바뀐 내용이 없을 때 (글이 수정되지 않았습니다. 돌아가시겠습니까?) " +
                                "| 401 -> 로그인된 Id와 글 쓴 사람 Id가 다를 때 or user 없을 때 (권한 없음) " +
                                "| 404 -> 삭제됐거나 찾을 수 없는 post " +
                                "| 409 -> 수정한 글 내용이 0글자 or 1000글자 초과일 때 (사진 있을 경우 0 허용)" )
    @PatchMapping(value = "/{postId}/update", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity update(@PathVariable("postId") int postId,
                                 @RequestBody EventRequest eventRequest,
                                 HttpServletRequest request) {

        logger.info("[PostController] update post.");
        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>
                    (this.postServiceImpl.updatePost(postId, eventRequest, request), header, HttpStatus.OK);
        }
        catch(BadRequestException no_change) {
            logger.info("[PostController] There is NO CHANGE to update.");

            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        catch(UnauthorizedUserException unauthorized_user) {
            logger.error("[PostController] This user is NOT authorized to delete.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoInformationException no_post) {
            logger.error("[PostController] Can NOT find (or already deleted) post by post-id.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(NoStoringException too_long_or_short_content) {
            logger.error("[PostController] The content is empty or too long.");

            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @ApiOperation(value = "1개 글 상세보기 (request : 글 Id)",
                notes = "response : 200 -> 성공 " +
                                "| 400 -> Private 글인데 본인(log-in된 Id) 글이 아닐 때 " +
                                "| 401 -> user 없을 경우 (권한 없음) " +
                                "| 404 -> 해당 post가 이미 지워짐")
    @GetMapping(value = "/{postId}/detail", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity detail(@PathVariable("postId") int postId,
                                 HttpServletRequest request) {

        logger.info("[PostController] get one post by post-id. ");
        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>
                    (this.postServiceImpl.getOnePostByPostId(postId, request), header, HttpStatus.OK);
        }
        catch(BadRequestException access_denied) {
            logger.error("[PostController] CANNOT access post because of FOLLOWERS or PRIVATE show level.");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch(UnauthorizedUserException no_user) {
            logger.info("[PostController] No user.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoInformationException no_post) {
            logger.error("[PostController] Can NOT find post by post-id.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

