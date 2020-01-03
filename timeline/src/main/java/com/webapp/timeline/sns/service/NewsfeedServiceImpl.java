package com.webapp.timeline.sns.service;

import com.amazonaws.services.s3.model.Owner;
import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.sns.domain.Comments;
import com.webapp.timeline.sns.domain.Likes;
import com.webapp.timeline.sns.domain.Newsfeed;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.dto.response.*;
import com.webapp.timeline.sns.repository.CommentsRepository;
import com.webapp.timeline.sns.repository.LikesRepository;
import com.webapp.timeline.sns.repository.NewsfeedRepository;
import com.webapp.timeline.sns.service.interfaces.NewsfeedService;
import com.webapp.timeline.sns.service.interfaces.SnsResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.webapp.timeline.sns.common.CommonTypeProvider.NEWSFEED_COMMENT;
import static com.webapp.timeline.sns.common.CommonTypeProvider.NEWSFEED_LIKE;

@Service
public class NewsfeedServiceImpl implements NewsfeedService, SnsResponseHelper<NewsfeedResponse, Newsfeed> {

    private static final Logger logger = LoggerFactory.getLogger(NewsfeedServiceImpl.class);
    private NewsfeedRepository newsfeedRepository;
    private CommentsRepository commentsRepository;
    private LikesRepository likesRepository;
    private TimelineServiceImpl timelineService;
    private ServiceAspectFactory<Newsfeed> factory;
    private static final int MAX_PRINTED_USERS = 2;

    NewsfeedServiceImpl() {
    }

    @Autowired
    NewsfeedServiceImpl(NewsfeedRepository newsfeedRepository,
                        CommentsRepository commentsRepository,
                        LikesRepository likesRepository,
                        TimelineServiceImpl timelineService,
                        ServiceAspectFactory<Newsfeed> factory) {
        this.newsfeedRepository = newsfeedRepository;
        this.commentsRepository = commentsRepository;
        this.likesRepository = likesRepository;
        this.timelineService = timelineService;
        this.factory = factory;
    }

    @Transactional
    @Override
    public SnsResponse<NewsfeedResponse> dispatch(Pageable pageable,
                                                  HttpServletRequest request) {
        logger.info("[NewsfeedService] dispatch list.");

        String loggedIn = factory.extractLoggedInAndActiveUser(request)
                                 .getUserId();

        Page<Newsfeed> feedList = newsfeedRepository.getNewsfeedByReceiver(pageable, loggedIn);

        if(factory.isPageExceed(feedList, pageable)) {
            throw new BadRequestException();
        }

        return makeSnsResponse(feedList, loggedIn);
    }

    @Override
    @SuppressWarnings("unchecked")
    public NewsfeedResponse makeSingleResponse(Newsfeed newsfeed, String loggedIn) {
        Posts post = null;

        try {
            post = factory.checkDeleteAndGetIfExist(newsfeed.getPostId());
        }
        catch(NoInformationException deleted_post) {
            throw new NoStoringException();
        }

        int postId = Objects.requireNonNull(post, "post already deleted.")
                            .getPostId();

        LinkedList tags = (LinkedList) timelineService.getPostTags(postId);
        String category = newsfeed.getCategory();
        List<Comments> postComments = commentsRepository.getCommentsByPostId(postId);
        List<String> postLikes = likesRepository.getLikesByPostId(postId);
        LinkedList<CommentResponse> selectedComments = new LinkedList<>();
        String isLoggedInUserLikeIt = "block";
        Likes likeObject = Likes.builder()
                                .postId(postId)
                                .owner(loggedIn)
                                .build();

        if(this.likesRepository.isUserLikedPost(likeObject) != null
                && this.likesRepository.isUserLikedPost(likeObject) > 0) {
            isLoggedInUserLikeIt = "none";
        }

        TimelineResponse feed = TimelineResponse.builder()
                                                .postId(postId)
                                                .profile(factory.makeSingleProfile(post.getAuthor()))
                                                .content(post.getContent())
                                                .showLevel(post.getShowLevel())
                                                .timestamp(timelineService.printEasyTimestamp(post.getLastUpdate()))
                                                .files(timelineService.getPostImages(postId))
                                                .tags(tags)
                                                .totalTag(tags.size())
                                                .totalComment(postComments.size())
                                                .totalLike(postLikes.size())
                                                .isLoggedInUserLikeIt(isLoggedInUserLikeIt)
                                                .build();

        AtomicInteger index = new AtomicInteger();
        AtomicReference senderNames = new AtomicReference("");

        String message = "";
        List<String> myFollowList = factory.followsWho(loggedIn);
        LinkedList<Map<String, String>> senderInfoList = new LinkedList<>();
        Map<String, String> senderInfo;

        if(category.equals(NEWSFEED_COMMENT)) {
            LinkedList<String> tempList = new LinkedList<>();

            for (Comments comment : postComments) {
                if (!myFollowList.contains(comment.getAuthor())) {
                    continue;
                }

                ProfileResponse profile = factory.makeSingleProfile(comment.getAuthor());

                selectedComments.add(CommentResponse.builder()
                                .postId(postId)
                                .profile(profile)
                                .content(comment.getContent())
                                .timestamp(timelineService.printEasyTimestamp(comment.getLastUpdate()))
                                .build());

                if (tempList.size() > 0 && tempList.contains(comment.getAuthor())) {
                    continue;
                }

                senderInfo = new HashMap<>(MAX_PRINTED_USERS);

                senderInfo.put("username", comment.getAuthor());
                senderInfo.put("nickname", profile.getName());
                senderInfoList.add(senderInfo);

                tempList.add(comment.getAuthor());

            }

            for (Map<String, String> info : senderInfoList) {
                if (index.get() == MAX_PRINTED_USERS - 1 && senderInfoList.size() > MAX_PRINTED_USERS - 1) {
                    senderNames.set(senderNames + ", ");
                }

                senderNames.set(senderNames + info.get("nickname") + "님");
                index.incrementAndGet();
            }

            if (senderInfoList.size() <= MAX_PRINTED_USERS) {
                message = senderNames.get() + "이 이 게시물에 댓글을 남겼습니다.";
            }
            else {
                message = senderNames.get() + " 외 " + (senderInfoList.size() - MAX_PRINTED_USERS) + "명이 이 게시물에 댓글을 남겼습니다.";
            }
        }
        else if (category.equals(NEWSFEED_LIKE)) {

            for (String owner : postLikes) {
                if (!myFollowList.contains(owner)) {
                    continue;
                }

                if (index.get() == MAX_PRINTED_USERS - 1) {
                    senderNames.set(senderNames + ", ");
                }

                String nickname = factory.loadUserById(owner)
                                         .getName();

                senderInfo = new HashMap<>(MAX_PRINTED_USERS);

                senderInfo.put("username", owner);
                senderInfo.put("nickname", nickname);
                senderInfoList.addLast(senderInfo);

                if (index.get() <= MAX_PRINTED_USERS - 1) {
                    senderNames.set(senderNames + nickname + "님");
                }

                index.getAndIncrement();
            }

            if (newsfeed.getFrequency() <= MAX_PRINTED_USERS) {
                message = senderNames.get() + "이 이 게시물을 좋아합니다.";
            }
            else if (newsfeed.getFrequency() > MAX_PRINTED_USERS) {
                message = senderNames.get() + "외 " + (newsfeed.getFrequency() - MAX_PRINTED_USERS) + "명이 이 게시물을 좋아합니다.";
            }
        }

        return NewsfeedResponse.builder()
                                .feed(feed)
                                .feedAuthorId(post.getAuthor())
                                .sender(senderInfoList)
                                .category(category)
                                .message(message)
                                .comment(selectedComments)
                                .build();
    }
}

