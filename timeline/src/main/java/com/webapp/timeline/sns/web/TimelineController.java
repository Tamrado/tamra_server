package com.webapp.timeline.sns.web;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.UnauthorizedUserException;
import com.webapp.timeline.sns.dto.request.CustomPageRequest;
import com.webapp.timeline.sns.service.TimelineServiceImpl;
import com.webapp.timeline.sns.service.interfaces.TimelineService;
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

@Api(tags = {"6. Timeline"})
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping(value = "/api/post")
public class TimelineController {
    private static final Logger logger = LoggerFactory.getLogger(TimelineController.class);
    private TimelineService timelineService;
    private HttpHeaders header;

    TimelineController() {
        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Autowired
    public void setTimelineService(TimelineServiceImpl timelineService) {
        this.timelineService = timelineService;
    }

    @ApiOperation(value = "내/ 다른 사용자의 프로필 - 메인화면(글 목록) (request : 유저 Id, 몇 page)",
            notes = "response : 200 -> 성공 " +
                    "| 400 -> 페이지 번호 > 마지막 페이지 일때 " +
                    "| 401 -> 로그인 안함/ 접근 권한 없을 때 " +
                    "| 404 -> 비활성 유저 (내 아이디여도 못 봄) ")
    @GetMapping(value = "/{userId}/timeline")
    public ResponseEntity listByUser(@PathVariable("userId") String userId,
                                     CustomPageRequest pageRequest,
                                     HttpServletRequest request) {

        logger.info("[TimelineController] get post-list by user-id.");

        try {
            return new ResponseEntity<>
                    (this.timelineService.loadPostListByUser(userId, pageRequest.of("postId"), request), header, HttpStatus.OK);
        }
        catch(BadRequestException exceed_page) {
            logger.error("[TimelineController] Input page exceeds last page.");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch(UnauthorizedUserException no_user) {
            logger.error("[TimelineController] No user.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoInformationException inactive_user) {
            logger.warn("[TimelineController] Inactive User.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "내/ 다른 사용자의 프로필 - 게시물 개수 (request : 유저 Id)",
            notes = "response : 200 -> 성공 " +
                    "| 401 -> 로그인 안함/ 접근 권한 없을 때 " +
                    "| 404 -> 비활성 유저 (내 아이디여도 못 봄) ")
    @GetMapping(value = "/{userId}/postnum")
    public ResponseEntity postNumByUser(@PathVariable("userId") String userId,
                                        HttpServletRequest request) {

        logger.info("[TimelineController] get total post-number by user-id.");

        try {
            return new ResponseEntity<>
                    (this.timelineService.loadPostNumberByUser(userId, request), header, HttpStatus.OK);
        }
        catch(UnauthorizedUserException no_user) {
            logger.error("[TimelineController] No user.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoInformationException inactive_user) {
            logger.warn("[TimelineController] Inactive User.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
