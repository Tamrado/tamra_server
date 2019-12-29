package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.sns.domain.Comments;
import com.webapp.timeline.sns.domain.Newsfeed;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.dto.response.CommentResponse;
import com.webapp.timeline.sns.dto.response.NewsfeedResponse;
import com.webapp.timeline.sns.dto.response.SnsResponse;
import com.webapp.timeline.sns.dto.response.TimelineResponse;
import com.webapp.timeline.sns.repository.CommentsRepository;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.webapp.timeline.sns.common.CommonTypeProvider.NEWSFEED_COMMENT;

@Service
public class NewsfeedServiceImpl implements NewsfeedService, SnsResponseHelper<NewsfeedResponse, Newsfeed> {

    private static final Logger logger = LoggerFactory.getLogger(NewsfeedServiceImpl.class);
    private NewsfeedRepository newsfeedRepository;
    private CommentsRepository commentsRepository;
    private TimelineServiceImpl timelineService;
    private ServiceAspectFactory<Newsfeed> factory;

    NewsfeedServiceImpl() {
    }

    @Autowired
    NewsfeedServiceImpl(NewsfeedRepository newsfeedRepository,
                        CommentsRepository commentsRepository,
                        TimelineServiceImpl timelineService,
                        ServiceAspectFactory<Newsfeed> factory) {
        this.newsfeedRepository = newsfeedRepository;
        this.commentsRepository = commentsRepository;
        this.timelineService = timelineService;
        this.factory = factory;
    }

    @Override
    public SnsResponse<NewsfeedResponse> dispatch(Pageable pageable,
                                                  HttpServletRequest request) {
        logger.info("[NewsfeedService] dispatch list.");

        String loggedIn = factory.extractLoggedIn(request);
        factory.checkInactiveUser(loggedIn);

        Page<Newsfeed> feedList = newsfeedRepository.getNewsfeedByReceiver(pageable, loggedIn);

        if(factory.isPageExceed(feedList, pageable)) {
            throw new BadRequestException();
        }

        return makeSnsResponse(feedList);
    }

    @Override
    @SuppressWarnings("unchecked")
    public NewsfeedResponse makeSingleResponse(Newsfeed newsfeed) {
        Posts post = null;
        LinkedList<CommentResponse> selectedComments = new LinkedList<>();

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

        if(category.equals(NEWSFEED_COMMENT)) {
            postComments.forEach(comment -> {
                if(!comment.getAuthor().equals(newsfeed.getSender())) {
                    return;
                }

                selectedComments.add(CommentResponse.builder()
                                                    .postId(postId)
                                                    .profile(factory.makeSingleProfile(newsfeed.getSender()))
                                                    .content(comment.getContent())
                                                    .timestamp(timelineService.printEasyTimestamp(comment.getLastUpdate()))
                                                    .build());
            });
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
                                                .totalLike((int) timelineService.countPostLikes(postId))
                                                .build();
        return NewsfeedResponse.builder()
                                .feed(feed)
                                .sender(newsfeed.getSender())
                                .category(category)
                                .comment(selectedComments)
                                .build();
    }
}
