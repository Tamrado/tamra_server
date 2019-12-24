package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.exception.UnauthorizedUserException;
import com.webapp.timeline.exception.WrongCodeException;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import static com.webapp.timeline.sns.common.CommonTypeProvider.DELETED_EVENT_CHECK;

@Service
public class ServiceAspectFactory<T> {
    private UserSignServiceImpl userSignService;
    private PostsRepository postsRepository;
    private UserImagesRepository userImagesRepository;

    ServiceAspectFactory() {
    }

    @Autowired
    public ServiceAspectFactory (UserSignServiceImpl userSignService,
                                 PostsRepository postsRepository,
                                 UserImagesRepository userImagesRepository) {
        this.userSignService = userSignService;
        this.postsRepository = postsRepository;
        this.userImagesRepository = userImagesRepository;
    }

    protected String extractLoggedIn(HttpServletRequest request) {
        String loggedIn = "";
        try {
            loggedIn = this.userSignService.extractUserFromToken(request)
                                        .getUserId();
        }
        catch(NoInformationException no_user) {
            throw new UnauthorizedUserException();
        }

        return loggedIn;
    }

    protected Map<String, String> getUserProfile(String userId) {
        String profile = this.userImagesRepository.findImageURLById(userId)
                                                .getProfileURL();
        String nickname = this.userSignService.loadUserByUsername(userId)
                                            .getUsername();

        return Collections.singletonMap(nickname, profile);
    }

    protected Posts checkDeleteAndGetIfExist(int postId) {
        Posts post = this.postsRepository.findById(postId)
                                        .orElseThrow(NoInformationException::new);
        if(post.getDeleted() == DELETED_EVENT_CHECK) {
            throw new NoInformationException();
        }

        return post;
    }

    protected Timestamp whatIsTimestampOfNow() {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        String now = LocalDateTime.now()
                .atZone(zoneId)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return Timestamp.valueOf(now);
    }

    protected void takeActionByQuery(int affectedRow) {
        if(affectedRow == 0) {
            throw new NoInformationException();
        }
    }

    protected void checkContentLength(String content, int maxLength) {
        if(content.length() == 0 || content.length() > maxLength) {
            throw new NoStoringException();
        }
    }

    protected void checkContentLengthIfImageExists(String content, int maxLength) {
        if(content.length() > maxLength) {
            throw new NoStoringException();
        }
    }

    protected boolean isPageExceed(Page<T> pagingList, Pageable pageable) {
        int current = pageable.getPageNumber();
        int lastPage = pagingList.getTotalPages() - 1;

        if(current > lastPage) {
            return true;
        }
        return false;
    }

}
