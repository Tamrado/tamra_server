package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Images;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.dto.ImageResponse;
import com.webapp.timeline.sns.dto.PagingResponse;
import com.webapp.timeline.sns.dto.TimelineResponse;
import com.webapp.timeline.sns.repository.ImagesRepository;
import com.webapp.timeline.sns.repository.PostsRepository;
import com.webapp.timeline.sns.service.interfaces.TimelineResponseHelper;
import com.webapp.timeline.sns.service.interfaces.TimelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TimelineServiceImpl implements TimelineService, TimelineResponseHelper {

    private static final Logger logger = LoggerFactory.getLogger(TimelineServiceImpl.class);
    private UserSignServiceImpl userSignService;
    private UserImagesRepository userImagesRepository;
    private PostsRepository postsRepository;
    private ImagesRepository imagesRepository;
    private ServiceAspectFactory<Posts> factory;
    private static final String INACTIVE_USER = "ROLE_INACTIVEUSER";
    private static final int ONE_HOUR = 60;
    private static final int ONE_MINUTE = 60;
    private final int TOTAL_IMAGE_MAX = 10;

    TimelineServiceImpl() {
    }

    @Autowired
    public TimelineServiceImpl(UserSignServiceImpl userSignService,
                               UserImagesRepository userImagesRepository,
                               PostsRepository postsRepository,
                               ImagesRepository imagesRepository,
                               ServiceAspectFactory<Posts> factory) {
        this.userSignService = userSignService;
        this.userImagesRepository = userImagesRepository;
        this.postsRepository = postsRepository;
        this.imagesRepository = imagesRepository;
        this.factory = factory;
    }

    @Override
    public PagingResponse<TimelineResponse> loadPostListByUser(String userId,
                                                               Pageable pageable,
                                                               HttpServletRequest request) {
        logger.info("[PostService] get post-list by user-id.");
        String loggedIn;
        Page<Posts> pagingPostList;

        checkInactiveUser(userId);

        try {
            loggedIn = this.userSignService.extractUserFromToken(request)
                                        .getUserId();

            if(loggedIn.equals(userId)) {
                pagingPostList = this.postsRepository.listMyPostsByUser(pageable, loggedIn);
            }
            else {
                pagingPostList = this.postsRepository.listPublicPostsByUser(pageable, userId);
            }
        }
        catch(NoMatchPointException not_logged_in) {
            pagingPostList = this.postsRepository.listPublicPostsByUser(pageable, userId);
        }

        if(factory.isPageExceed(pagingPostList, pageable)) {
            throw new BadRequestException();
        }

        //Todo : following 중인지 검사 -> following중이면 followers 허용 글까지 볼 수 있게
        return makeResponseObject(pagingPostList, userImagesRepository, userSignService);
    }

    private void checkInactiveUser(String userId) {
        Users userInfo = this.userSignService.loadUserByUsername(userId);

        if(userInfo.getAuthority().equals(INACTIVE_USER)) {
            throw new NoInformationException();
        }
    }

    @Override
    public List getPostImages(int postId) {
        List<ImageResponse> imageResponses = new LinkedList<>();
        AtomicInteger count = new AtomicInteger();
        Optional<List<Images>> image = Optional.ofNullable(this.imagesRepository.listImageListInPost(postId));

        //TODO : null 처리
        if(!image.isPresent()) {
            return Collections.EMPTY_LIST;
        }

        image.get().forEach(object -> {
            if(count.get() == TOTAL_IMAGE_MAX) {
                return;
            }

            imageResponses.add(ImageResponse.builder()
                                            .original(object.getUrl())
                                            .thumbnail(object.getThumbnail())
                                            .build());
            count.getAndIncrement();
        });

        return imageResponses;
    }


    @Override
    public String printEasyTimestamp(Timestamp time) {
        LocalDateTime responsedItem = time.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        if(responsedItem.isAfter(now.minus(1, ChronoUnit.HOURS))) {
            int timestamp;

            if(responsedItem.isAfter(now.minus(1, ChronoUnit.MINUTES))) {
                int secondDifference = now.getSecond() - responsedItem.getSecond();
                timestamp = secondDifference >= 0 ? secondDifference : secondDifference + ONE_MINUTE;

                return timestamp + "초 전";
            }

            int minuteDifference = now.getMinute() - responsedItem.getMinute();
            timestamp = minuteDifference > 0 ? minuteDifference : minuteDifference + ONE_HOUR;

            return timestamp + "분 전";
        }

        return new SimpleDateFormat("yyyy.MM.dd").format(time);
    }

}
