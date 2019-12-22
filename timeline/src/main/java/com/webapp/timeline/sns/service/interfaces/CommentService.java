package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.domain.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    Comments registerComment(int postId, Comments comment, HttpServletRequest request);

    Comments removeComment(long commentId, HttpServletRequest request);

    Comments editComment(long commentId, Comments comment, HttpServletRequest request);

    Page<Comments> listAllCommentsByPostId(Pageable pageable, int postId);
}
