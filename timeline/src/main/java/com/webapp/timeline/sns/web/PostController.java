package com.webapp.timeline.sns.web;

import com.webapp.timeline.exception.*;
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
                notes="response : 201 -> 성공 " +
                                "| 400 -> 글 내용이 없을 때 " +
                                "| 411 -> 글 내용 글자수 255글자 초과 시")
    @PostMapping(value = "/upload", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity create(@RequestBody Posts post,
                                 @ApiIgnore HttpServletRequest request) {

        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>(this.postServiceImpl.createPost(post, request), header, HttpStatus.CREATED);
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

    @ApiOperation(value = "글 삭제하기 (request : 글 Id)",
                notes = "response : 200 -> 성공 " +
                                "| 401 -> 로그인된 Id와 글 쓴 사람 Id가 다를 때 " +
                                "| 404 -> 해당 post를 찾을 수 없음 " +
                                "| 422 -> 삭제가 반영되지 않았을 때 (아직 글 남아있음)")
    @PutMapping(value = "/{postId}/delete")
    public ResponseEntity delete(@PathVariable("postId") int postId,
                                 @ApiIgnore HttpServletRequest request) {

        logger.info("[PostController] delete post.");
        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try {
            return new ResponseEntity<>(this.postServiceImpl.deletePost(postId, request), header, HttpStatus.OK);
        }
        catch(UnauthorizedUserException unauthorized_user) {
            logger.error("[PostController] This user is NOT authorized to delete.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoInformationException no_post) {
            logger.error("[PostController] CanNOT find post by post-id.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(WrongCodeException no_affected_row) {
            logger.error("[PostController] There is 0 affected row.");

            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}

