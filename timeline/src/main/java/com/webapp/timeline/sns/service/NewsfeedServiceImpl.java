package com.webapp.timeline.sns.service;

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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.webapp.timeline.sns.common.CommonTypeProvider.*;

@Service
public class NewsfeedServiceImpl implements NewsfeedService, SnsResponseHelper<NewsfeedResponse, Newsfeed> {

    private static final Logger logger = LoggerFactory.getLogger(NewsfeedServiceImpl.class);
    private NewsfeedRepository newsfeedRepository;
    private CommentsRepository commentsRepository;
    private LikesRepository likesRepository;
    private TimelineServiceImpl timelineService;
    private ServiceAspectFactory<Newsfeed> factory;
    private static final int MAX_PRINTED_USERS = 1;

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

        int totalComment = (int) timelineService.countPostComments(postId);
	    boolean trueComment;

	    if(totalComment == 0) {
	        trueComment = false;
        }
	    else {
	        trueComment = true;
        }

        TimelineResponse feed = TimelineResponse.builder()
                                                .postId(postId)
                                                .profile(factory.makeSingleProfile(post.getAuthor()))
                                                .content(post.getContent())
                                                .showLevel(post.getShowLevel())
                                                .timestamp(new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(post.getLastUpdate().getTime()))
                                                .dateString("")
                                                .files(timelineService.getPostImages(postId))
                                                .tags(tags)
                                                .totalTag(tags.size())
                                                .totalComment(totalComment)
                                                .totalLike((int) timelineService.countPostLikes(postId))
                                                .isLoggedInUserLikeIt(isLoggedInUserLikeIt)
                                                .commentState(DEFAULT_COMMENT_STATE)
                                                .commentPage(DEFAULT_COMMENT_PAGE)
                                                .commentList(new ArrayList<CommentResponse>())
                                                .isTrueComment(trueComment)
                                                .build();

        String message = "";
        LinkedList<Map<String, String>> senderInfoList = new LinkedList<>();
        Map<String, String> senderInfo;

        if(category.equals(NEWSFEED_COMMENT)) {

            Optional<Comments> comment = commentsRepository.findById(newsfeed.getCommentId());
            if(! comment.isPresent()) {
                return null;
            }

            ProfileResponse profile = factory.makeSingleProfile(comment.get().getAuthor());

            selectedComments.add(CommentResponse.builder()
                            .postId(postId)
                            .profile(profile)
                            .commentId(newsfeed.getCommentId())
                            .content(comment.get().getContent())
                            .timestamp(new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(comment.get().getLastUpdate().getTime()))
                            .dateString("")
                            .build());

            senderInfo = new HashMap<>(MAX_PRINTED_USERS);

            senderInfo.put("username", comment.get().getAuthor());
            senderInfo.put("nickname", profile.getName());
            senderInfoList.add(senderInfo);

            message = profile.getName() + "님이 이 게시물에 댓글을 남겼습니다.";
        }
        else if (category.equals(NEWSFEED_LIKE)) {
            String nickname = factory.loadUserById(newsfeed.getSender())
                                    .getName();
            senderInfo = new HashMap<>(MAX_PRINTED_USERS);

            senderInfo.put("username", newsfeed.getSender());
            senderInfo.put("nickname", nickname);

            message = nickname + "님이 이 게시물을 좋아합니다.";
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

