package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Images;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.dto.ImageDto;
import com.webapp.timeline.sns.dto.response.SnsResponse;
import com.webapp.timeline.sns.dto.response.TimelineResponse;
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
import java.util.*;

import static com.webapp.timeline.sns.common.CommonTypeProvider.TOTAL_IMAGE_MAX;

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
    private static final int ONE_DAY = 24;

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
    public SnsResponse<TimelineResponse> loadPostListByUser(String userId,
                                                            Pageable pageable,
                                                            HttpServletRequest request) {
        logger.info("[PostService] get post-list by user-id.");
        String loggedIn;
        Page<Posts> pagingPostList;

        checkInactiveUser(userId);

        try {
            loggedIn = factory.extractLoggedIn(request);

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
        return makeSnsResponse(pagingPostList);
    }

    private void checkInactiveUser(String userId) {
        Users userInfo = this.userSignService.loadUserByUsername(userId);

        if(userInfo.getAuthority().equals(INACTIVE_USER)) {
            throw new NoInformationException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public TimelineResponse makeSingleResponse(Posts item) {
        Map<String, String> userInfo = getUserProfile(item.getAuthor());
        String nickname = userInfo.keySet()
                                .iterator()
                                .next();

        return TimelineResponse.builder()
                            .postId(item.getPostId())
                            .author(nickname)
                            .profile(userInfo.get(nickname))
                            .content(item.getContent())
                            .showLevel(item.getShowLevel())
                            .timestamp(printEasyTimestamp(item.getLastUpdate()))
                            .files(getPostImages(item.getPostId()))
                            .totalComment(Math.toIntExact(item.getCommentNum()))
                            .build();
    }

    protected Map<String, String> getUserProfile(String userId) {
        String profile = this.userImagesRepository.findImageURLById(userId)
                                                .getProfileURL();
        String nickname = this.userSignService.loadUserByUsername(userId)
                                            .getUsername();

        return Collections.singletonMap(nickname, profile);
    }

    private List getPostImages(int postId) {
        List<ImageDto> imageResponses = new LinkedList<>();
        Optional<List<Images>> image = Optional.ofNullable(this.imagesRepository.listImageListInPost(postId));
        if(!image.isPresent()) {
            return Collections.EMPTY_LIST;
        }

        image.get().forEach(object -> {
            if(imageResponses.size() == TOTAL_IMAGE_MAX) {
                return;
            }

            imageResponses.add(ImageDto.builder()
                                        .original(object.getUrl())
                                        .thumbnail(object.getThumbnail())
                                        .build());
        });

        return imageResponses;
    }

    protected String printEasyTimestamp(Timestamp time) {
        LocalDateTime responsedItem = time.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        int timestamp;

        if(responsedItem.isAfter(now.minus(1, ChronoUnit.HOURS))) {

            if(responsedItem.isAfter(now.minus(1, ChronoUnit.MINUTES))) {
                int secondDifference = now.getSecond() - responsedItem.getSecond();
                timestamp = secondDifference >= 0 ? secondDifference : secondDifference + ONE_MINUTE;

                if(timestamp == 0 || timestamp == 1) {
                    return "방금 전";
                }

                return timestamp + "초 전";
            }

            int minuteDifference = now.getMinute() - responsedItem.getMinute();
            timestamp = minuteDifference > 0 ? minuteDifference : minuteDifference + ONE_HOUR;

            return timestamp + "분 전";
        }
        else if(responsedItem.isAfter(now.minus(24, ChronoUnit.HOURS))) {
            int hourDifference = now.getHour() - responsedItem.getHour();
            timestamp = hourDifference > 0 ? hourDifference : hourDifference + ONE_DAY;

            return timestamp + "시간 전";
        }

        return new SimpleDateFormat("yyyy.MM.dd").format(time);
    }

}
