package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.*;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Comments;
import com.webapp.timeline.sns.repository.CommentsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    private CommentsRepository commentsRepository;
    private UserSignServiceImpl userSignServiceImpl;
    private static final int MAXIMUM_CONTENT_LENGTH = 300;
    private static final int NEW_COMMENT_CHECK = 0;
    private static final int REMOVED_COMMENT_CHECK = 1;

    @Autowired
    public void setCommentsRepository(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    @Autowired
    public void setUserSignServiceImpl(UserSignServiceImpl userSignServiceImpl) {
        this.userSignServiceImpl = userSignServiceImpl;
    }

    @Override
    public Comments registerComment(long postId, Comments comment, HttpServletRequest request) {
        logger.info("[CommentService] register comment.");
        String author;
        String content;

        author = this.userSignServiceImpl.extractUserFromToken(request)
                                        .getId();
        content = comment.getContent();

        if(content.length() == 0) {
            throw new BadRequestException();
        }
        else if(content.length() > MAXIMUM_CONTENT_LENGTH) {
            throw new NoMatchPointException();
        }

        Comments newComment = makeObjectForComment(postId, content, author);

        return commentsRepository.save(newComment);
    }

    private Comments makeObjectForComment(long postId, String content, String author) {

        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        String now = LocalDateTime.now()
                .atZone(zoneId)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new Comments.Builder()
                            .postId(postId)
                            .author(author)
                            .content(content)
                            .lastUpdate(Timestamp.valueOf(now))
                            .deleted(NEW_COMMENT_CHECK)
                            .build();
    }

    @Override
    public Comments removeComment(long commentId, HttpServletRequest request) {
        logger.info("[CommentService] remove comment.");
        String author;

        author = this.userSignServiceImpl.extractUserFromToken(request)
                                        .getId();
        Comments comment = this.commentsRepository.findById(commentId)
                                                .orElseThrow(NoInformationException::new);

        if(author.equals(comment.getAuthor())) {
            comment.setDeleted(REMOVED_COMMENT_CHECK);

            return tryQueryAndTakeAction(comment);
        }
        else
            throw new UnauthorizedUserException();
    }

    private Comments tryQueryAndTakeAction(Comments comment) {
        int affectedRow;

        affectedRow = this.commentsRepository.markDeleteByCommentId(comment);

        if(affectedRow == 1) {
            return comment;
        }
        else if(affectedRow == 0) {
            throw new WrongCodeException();
        }
        else {
            throw new InternalServerException();
        }
    }
}
