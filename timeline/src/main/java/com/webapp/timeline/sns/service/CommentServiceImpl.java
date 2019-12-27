package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.*;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import com.webapp.timeline.sns.domain.Comments;
import com.webapp.timeline.sns.dto.response.CommentResponse;
import com.webapp.timeline.sns.dto.response.SnsResponse;
import com.webapp.timeline.sns.repository.CommentsRepository;
import com.webapp.timeline.sns.service.interfaces.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import static com.webapp.timeline.sns.common.CommonTypeProvider.DELETED_EVENT_CHECK;
import static com.webapp.timeline.sns.common.CommonTypeProvider.NEW_EVENT_CHECK;


@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    private CommentsRepository commentsRepository;
    private TimelineServiceImpl timelineService;
    private ServiceAspectFactory<Comments> factory;
    private static final int MAXIMUM_CONTENT_LENGTH = 300;

    CommentServiceImpl() {
    }

    @Autowired
    public CommentServiceImpl(CommentsRepository commentsRepository,
                              TimelineServiceImpl timelineService,
                              ServiceAspectFactory<Comments> factory) {
        this.commentsRepository = commentsRepository;
        this.timelineService = timelineService;
        this.factory = factory;
    }

    @Override
    public CommentResponse registerComment(int postId, Comments comment, HttpServletRequest request) {
        logger.info("[CommentService] register comment.");
        String author;
        String content;

        factory.checkDeleteAndGetIfExist(postId);

        author = factory.extractLoggedIn(request);
        content = comment.getContent();
        factory.checkContentLength(content, MAXIMUM_CONTENT_LENGTH);

        Comments newComment = makeObjectForComment(postId, content, author);
        return makeSingleResponse(commentsRepository.save(newComment));
    }

    @Override
    public int removeCommentByPostId(int postId) {
        logger.info("[CommentService] remove all-comments by postId.");

        return this.commentsRepository.markDeleteByPostId(postId);
    }

    @Override
    public Map<String, Integer> removeComment(long commentId, HttpServletRequest request) {
        logger.info("[CommentService] remove comment.");

        Comments comment = this.commentsRepository.findById(commentId)
                                                .orElseThrow(NoInformationException::new);

        if(factory.extractLoggedIn(request).equals(comment.getAuthor())) {
            comment.setDeleted(DELETED_EVENT_CHECK);

            factory.takeActionByQuery(this.commentsRepository.markDeleteByCommentId(comment));
            return Collections.singletonMap("commentId", (int)commentId);
        }
        else
            throw new UnauthorizedUserException();
    }

    @Override
    public CommentResponse editComment(long commentId, Comments comment, HttpServletRequest request) {
        logger.info("[CommentService] edit comment.");

        Comments existedComment = this.commentsRepository.findById(commentId)
                                                        .orElseThrow(NoInformationException::new);

        if(existedComment.getDeleted() == DELETED_EVENT_CHECK) {
            throw new BadRequestException();
        }

        if(factory.extractLoggedIn(request).equals(existedComment.getAuthor())) {
            factory.checkContentLength(comment.getContent(), MAXIMUM_CONTENT_LENGTH);

            existedComment.setContent(comment.getContent());
            existedComment.setLastUpdate(factory.whatIsTimestampOfNow());

            factory.takeActionByQuery(this.commentsRepository.editCommentByCommentId(existedComment));
            return makeSingleResponse(existedComment);
        }
        else
            throw new UnauthorizedUserException();
    }

    @Override
    public SnsResponse<CommentResponse> listAllCommentsByPostId(Pageable pageable, int postId) {
        logger.info("[CommentService] list comments.");
        LinkedList<CommentResponse> eventList = new LinkedList<>();

        factory.checkDeleteAndGetIfExist(postId);
        Page<Comments> pagingCommentList = this.commentsRepository.listValidCommentsByPostId(pageable, postId);

        if(factory.isPageExceed(pagingCommentList, pageable)) {
            throw new BadRequestException();
        }

        pagingCommentList.forEach(item -> {
            eventList.add(makeSingleResponse(item));
        });

        return SnsResponse.<CommentResponse>builder()
                        .objectSet(eventList)
                        .first(pagingCommentList.isFirst())
                        .last(pagingCommentList.isLast())
                        .build();
    }

    private Comments makeObjectForComment(int postId, String content, String author) {

        return Comments.builder()
                        .postId(postId)
                        .author(author)
                        .content(content)
                        .lastUpdate(factory.whatIsTimestampOfNow())
                        .deleted(NEW_EVENT_CHECK)
                        .build();
    }

    private CommentResponse makeSingleResponse(Comments comment) {

        return CommentResponse.builder()
                            .postId(comment.getPostId())
                            .profile(factory.makeSingleProfile(comment.getAuthor()))
                            .content(comment.getContent())
                            .timestamp(timelineService.printEasyTimestamp(comment.getLastUpdate()))
                            .build();
    }
}
