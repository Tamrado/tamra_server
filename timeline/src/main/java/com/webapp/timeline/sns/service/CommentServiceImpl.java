package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.*;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Comments;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.repository.CommentsRepository;
import com.webapp.timeline.sns.repository.PostsRepository;
import com.webapp.timeline.sns.service.interfaces.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;


@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    private PostsRepository postsRepository;
    private CommentsRepository commentsRepository;
    private UserSignServiceImpl userSignServiceImpl;
    private ServiceAspectFactory<Comments> factory;
    private static final int MAXIMUM_CONTENT_LENGTH = 300;
    private static final int NEW_COMMENT_CHECK = 0;
    private static final int REMOVED_COMMENT_CHECK = 1;
    private static final int DELETED_POST = 1;

    @Autowired
    public void setPostsRepository(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    @Autowired
    public void setCommentsRepository(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    @Autowired
    public void setUserSignServiceImpl(UserSignServiceImpl userSignServiceImpl) {
        this.userSignServiceImpl = userSignServiceImpl;
    }

    @Autowired
    public void setServiceAspectExecutor(ServiceAspectFactory<Comments> factory) {
        this.factory = factory;
    }

    private void checkIfPostDeleted(long postId) {
        Posts post = this.postsRepository.findById((int) postId)
                                        .orElseThrow(NoInformationException::new);
        if(post.getDeleted() == DELETED_POST) {
            throw new NoInformationException();
        }
    }

    @Override
    public Comments registerComment(long postId, Comments comment, HttpServletRequest request) {
        logger.info("[CommentService] register comment.");
        String author;
        String content;

        checkIfPostDeleted(postId);

        author = this.userSignServiceImpl.extractUserFromToken(request)
                                        .getUserId();
        content = comment.getContent();
        factory.checkContentLength(content, MAXIMUM_CONTENT_LENGTH);

        Comments newComment = makeObjectForComment(postId, content, author);

        return commentsRepository.save(newComment);
    }

    private Comments makeObjectForComment(long postId, String content, String author) {

        return new Comments.Builder()
                            .postId(postId)
                            .author(author)
                            .content(content)
                            .lastUpdate(factory.whatIsTimestampOfNow())
                            .deleted(NEW_COMMENT_CHECK)
                            .build();
    }

    @Override
    public Comments removeComment(long commentId, HttpServletRequest request) {
        logger.info("[CommentService] remove comment.");
        String author;
        int affectedRow;

        author = this.userSignServiceImpl.extractUserFromToken(request)
                                        .getUserId();
        Comments comment = this.commentsRepository.findById(commentId)
                                                .orElseThrow(NoInformationException::new);

        checkIfPostDeleted(comment.getPostId());

        if(author.equals(comment.getAuthor())) {
            comment.setDeleted(REMOVED_COMMENT_CHECK);

            affectedRow = this.commentsRepository.markDeleteByCommentId(comment);

            return factory.takeActionByQuery(comment, affectedRow);
        }
        else
            throw new UnauthorizedUserException();
    }

    @Override
    public Comments editComment(long commentId, Comments comment, HttpServletRequest request) {
        logger.info("[CommentService] edit comment.");
        String author;
        int affectedRow;

        author = this.userSignServiceImpl.extractUserFromToken(request)
                                        .getUserId();
        Comments existedComment = this.commentsRepository.findById(commentId)
                                                        .orElseThrow(NoInformationException::new);
        checkIfPostDeleted(existedComment.getPostId());

        if(existedComment.getDeleted() == REMOVED_COMMENT_CHECK) {
            throw new BadRequestException();
        }

        if(author.equals(existedComment.getAuthor())) {
            factory.checkContentLength(comment.getContent(), MAXIMUM_CONTENT_LENGTH);

            existedComment.setContent(comment.getContent());
            existedComment.setLastUpdate(factory.whatIsTimestampOfNow());

            affectedRow = this.commentsRepository.editCommentByCommentId(existedComment);

            return factory.takeActionByQuery(existedComment, affectedRow);
        }
        else
            throw new UnauthorizedUserException();
    }

    @Override
    public Page<Comments> listAllCommentsByPostId(Pageable pageable, long postId) {
        logger.info("[CommentService] list comments.");
        Page<Comments> pagingCommentList;

        checkIfPostDeleted(postId);
        pagingCommentList = this.commentsRepository.listValidCommentsByPostId(pageable, postId);

        if(factory.isPageExceed(pagingCommentList, pageable)) {
            throw new BadRequestException();
        }

        return pagingCommentList;
    }
}
