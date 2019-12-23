package com.webapp.timeline.sns.web;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.UnauthorizedUserException;
import com.webapp.timeline.sns.service.LikeServiceImpl;
import com.webapp.timeline.sns.service.interfaces.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"5. Likes"})
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping(value = "/api/post")
public class LikeController {

    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);
    private LikeService likeService;

    LikeController() {
    }

    @Autowired
    public LikeController(LikeServiceImpl likeService) {
        this.likeService = likeService;
    }

    @ApiOperation(value = "좋아요 클릭 (request: 글 Id) ",
                notes = "response : 200 -> 성공 " +
                                "| 400 -> 이미 좋아한 글 " +
                                "| 401 -> user 없을 경우 (권한 없음) " +
                                "| 404 -> 이미 삭제된 글")
    @PostMapping(value = "/{postId}/like")
    public ResponseEntity heart(@PathVariable("postId") int postId,
                                HttpServletRequest request) {
        logger.info("[LikeController] Click heart.");

        try {
            this.likeService.clickHeart(postId, request);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(BadRequestException already_liked) {
            logger.warn("[LikeController] Already liked post.");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch(UnauthorizedUserException no_user) {
            logger.error("[LikeController] No user.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoInformationException no_post) {
            logger.error("[LikeController] The post already deleted.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "좋아요 취소 (request: 글 Id) ",
                notes = "response : 200 -> 성공 " +
                                "| 400 -> 이미 좋아요 취소한 글 " +
                                "| 401 -> user 없을 경우 (권한 없음) ")
    @DeleteMapping(value = "/{postId}/like/cancel")
    public ResponseEntity cancel(@PathVariable("postId") int postId,
                                 HttpServletRequest request) {
        logger.info("[LikeController] Cancel heart.");

        try {
            this.likeService.cancelHeart(postId, request);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(BadRequestException already_liked) {
            logger.warn("[LikeController] Already liked post.");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch(UnauthorizedUserException no_user) {
            logger.error("[LikeController] No user.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(value = "1개 글을 좋아한 유저 목록 + 총 개수 (request: 글 Id, 몇 page)")
}
