package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import com.webapp.timeline.sns.domain.Images;
import com.webapp.timeline.sns.domain.Likes;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.dto.response.ImageResponse;
import com.webapp.timeline.sns.dto.response.SnsResponse;
import com.webapp.timeline.sns.dto.response.TimelineResponse;
import com.webapp.timeline.sns.repository.*;
import com.webapp.timeline.sns.service.interfaces.SnsResponseHelper;
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

import static com.webapp.timeline.sns.common.CommonTypeProvider.*;
import static com.webapp.timeline.sns.common.ShowTypeProvider.*;

@Service
public class TimelineServiceImpl implements TimelineService, SnsResponseHelper<TimelineResponse, Posts> {

    private static final Logger logger = LoggerFactory.getLogger(TimelineServiceImpl.class);
    private PostsRepository postsRepository;
    private ImagesRepository imagesRepository;
    private TagsRepository tagsRepository;
    private CommentsRepository commentsRepository;
    private LikesRepository likesRepository;
    private ServiceAspectFactory<Posts> factory;
    private static final int ONE_HOUR = 60;
    private static final int ONE_MINUTE = 60;
    private static final int ONE_DAY = 24;

    public TimelineServiceImpl() {
    }

    @Autowired
    public TimelineServiceImpl(PostsRepository postsRepository,
                               ImagesRepository imagesRepository,
                               TagsRepository tagsRepository,
                               CommentsRepository commentsRepository,
                               LikesRepository likesRepository,
                               ServiceAspectFactory<Posts> factory) {
        this.postsRepository = postsRepository;
        this.imagesRepository = imagesRepository;
        this.tagsRepository = tagsRepository;
        this.commentsRepository = commentsRepository;
        this.likesRepository = likesRepository;
        this.factory = factory;
    }

    @Override
    public SnsResponse<TimelineResponse> loadPostListByUser(String userId,
                                                            Pageable pageable,
                                                            HttpServletRequest request) {
        logger.info("[TimelineService] get post-list by user-id.");
        String loggedIn = factory.extractLoggedInAndActiveUser(request)
                                 .getUserId();
        Page<Posts> userTimeline = dispatchByAccessScope(userId, pageable, loggedIn);

        if(factory.isPageExceed(userTimeline, pageable)) {
            throw new BadRequestException();
        }

        return makeSnsResponse(userTimeline, loggedIn);
    }

    @Override
    public long loadPostNumberByUser(String userId, HttpServletRequest request) {
        logger.info("[TimelineService] get total post-number by user-id.");

        factory.checkInactiveUser(userId);
        String loggedIn = factory.extractLoggedIn(request);

        if(userId.equals(loggedIn)) {
            return this.postsRepository.showPostNumberByUserWhenMyId(userId);
        }

        return this.postsRepository.showPostNumberByUser(userId);
    }

    private Page<Posts> dispatchByAccessScope(String author, Pageable pageable, String loggedIn) {
        String subscribe = "";

        if(author.equals(loggedIn)) {
            subscribe = PRIVATE_TYPE.getName();
        }
        else if(factory.isFollowedMe(loggedIn, author)) {
            subscribe = FOLLOWER_TYPE.getName();
        }
        else {
            subscribe = PUBLIC_TYPE.getName();
        }

        return this.postsRepository.showTimelineByUser(pageable, author, subscribe);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TimelineResponse makeSingleResponse(Posts item, String loggedIn) {
        int postId = item.getPostId();
        List tags = getPostTags(postId);
        String isLoggedInUserLikeIt = "block";
        Likes likeObject = Likes.builder()
                                .postId(postId)
                                .owner(loggedIn)
                                .build();

        if(this.likesRepository.isUserLikedPost(likeObject) != null &&
                this.likesRepository.isUserLikedPost(likeObject) > 0) {
            isLoggedInUserLikeIt = "none";
        }


        return TimelineResponse.builder()
                            .postId(postId)
                            .profile(factory.makeSingleProfile(item.getAuthor()))
                            .content(item.getContent())
                            .showLevel(item.getShowLevel())
                            .timestamp(new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(item.getLastUpdate()))
                            .dateString("")
                            .files(getPostImages(postId))
                            .tags(tags)
                            .totalTag(tags.size())
                            .totalComment((int) countPostComments(postId))
                            .totalLike((int) countPostLikes(postId))
                            .isLoggedInUserLikeIt(isLoggedInUserLikeIt)
                            .commentState(DEFAULT_COMMENT_STATE)
                            .commentPage(DEFAULT_COMMENT_PAGE)
                            .build();
    }

    List getPostImages(int postId) {
        List<ImageResponse> imageResponses = new LinkedList<>();
        Optional<List<Images>> image = Optional.ofNullable(this.imagesRepository.listImageListInPost(postId));

        if(!image.isPresent()) {
            return Collections.EMPTY_LIST;
        }

        image.get().forEach(object -> {
            if(imageResponses.size() == TOTAL_IMAGE_MAX) {
                return;
            }

            imageResponses.add(ImageResponse.builder()
                                        .original(object.getUrl())
                                        .thumbnail(object.getThumbnail())
                                        .build());
        });

        return imageResponses;
    }

    List getPostTags(int postId) {
        List<LoggedInfo> tagResponses = new LinkedList<>();
        Optional<List<String>> tags = Optional.ofNullable(this.tagsRepository.listTagListInPost(postId));

        if(!tags.isPresent()) {
            return Collections.EMPTY_LIST;
        }

        tags.get().forEach(username -> {
            tagResponses.add(factory.getUserInfo(username));
        });

        return tagResponses;
    }

    private long countPostComments(int postId) {
        return this.commentsRepository.countCommentsByPostId(postId);
    }

    long countPostLikes(int postId) {
        return this.likesRepository.countLikesByPostId(postId);
    }

    String printEasyTimestamp(Timestamp time) {
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
