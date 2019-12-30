package com.webapp.timeline.sns.web;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.exception.UnauthorizedUserException;
import com.webapp.timeline.sns.dto.request.CustomPageRequest;
import com.webapp.timeline.sns.service.NewsfeedServiceImpl;
import com.webapp.timeline.sns.service.interfaces.NewsfeedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"6. Timeline"})
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping(value = "/api/post")
public class NewsfeedController {

    private static final Logger logger = LoggerFactory.getLogger(NewsfeedController.class);
    private NewsfeedService newsfeedService;
    private HttpHeaders header;

    NewsfeedController() {
        header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Autowired
    NewsfeedController(NewsfeedServiceImpl newsfeedService) {
        this.newsfeedService = newsfeedService;
    }

    @ApiOperation(value = "메인페이지 (request: 몇 페이지)",
            notes = "response : 200 -> 성공 " +
                    "| 400 -> 페이지 번호 > 마지막 페이지 일때 " +
                    "| 401 -> 로그인 안함/ 접근 권한 없을 때 " +
                    "| 404 -> 비활성 계정으로 로그인함 " +
                    "| 409 -> 삭제된 게시물 (페이지 불러오고 나서 삭제됨)")
    @GetMapping(value = "/newsfeed")
    public ResponseEntity newsfeed(CustomPageRequest pageRequest,
                                   HttpServletRequest request) {
        logger.info("[NewsfeedController] Main Page.");

        try {
            return new ResponseEntity<>
                    (newsfeedService.dispatch(pageRequest.of("id"), request), header, HttpStatus.OK);
        }
        catch(BadRequestException exceed_page) {
            logger.error("[NewsfeedController] Input page exceeds last page.");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch(UnauthorizedUserException no_user) {
            logger.error("[NewsfeedController] No user.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch(NoInformationException inactive_user) {
            logger.warn("[NewsfeedController] Inactive User.");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(NoStoringException deleted_post) {
            logger.error("[NewsfeedController] Post alreay deleted.");

            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
