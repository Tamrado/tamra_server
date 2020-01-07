package com.webapp.timeline.sns.web;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.UnauthorizedUserException;
import com.webapp.timeline.sns.dto.request.CustomPageRequest;
import com.webapp.timeline.sns.service.interfaces.EventService;
import com.webapp.timeline.sns.service.EventServiceImpl;
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

@Api(tags = {"7. Alarm"})
@RestController
@RequestMapping(value = "/api/post/event")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private EventService eventService;
    private HttpHeaders header;

    EventController() {
        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Autowired
    public EventController(EventServiceImpl actionService) {
        this.eventService = actionService;
    }

    @ApiOperation(value = "메인페이지에서 유저의 태그 알람 내역 불러오기",
            notes = "response : 200 -> 성공 " +
                            "| 401 -> 로그인 안함/ 접근 권한 없을 때 " +
                            "| 404 -> 비활성 유저 (내 아이디여도 못 봄) ")
    @GetMapping("/fetch")
    public ResponseEntity fetch(HttpServletRequest request) {
        logger.info("[EventController] Alarm for tag activities.");

        try {
            return new ResponseEntity<>(eventService.fetchActivities(request), header, HttpStatus.OK);
        }
        catch(UnauthorizedUserException no_user) {
            logger.error("[EventController] No user.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoInformationException inactive_user) {
            logger.warn("[EventController] Inactive User.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "모두 읽은 상태로 표시",
            notes = "response : 200 -> 성공 " +
                            "| 401 -> 로그인 안함/ 접근 권한 없을 때 ")
    @PutMapping("/all/read")
    public ResponseEntity allRead(HttpServletRequest request) {
        logger.info("[EventController] Make tag-activities all read.");

        try {
            eventService.makeEventsAllRead(request);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(UnauthorizedUserException no_user) {
            logger.error("[EventController] No user.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(value = "태그 알림 개수 받기",
            notes = "response : 200 -> 성공 " +
                    "| 401 -> 로그인 안함/ 접근 권한 없을 때 " )
    @GetMapping("/count")
    public ResponseEntity countTagAlarm(HttpServletRequest request) {
        logger.info("[EventController] Count new tag-activities.");
        try {
            return new ResponseEntity<>(eventService.countEvents(request), header, HttpStatus.OK);
        }
        catch(UnauthorizedUserException no_user) {
            logger.error("[EventController] No user.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
