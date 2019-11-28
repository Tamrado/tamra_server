package com.webapp.timeline.sns.service;

import com.webapp.timeline.sns.domain.Comments;

import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    Comments registerComment(long postId, Comments comment, HttpServletRequest request);

    Comments removeComment(long commentId, HttpServletRequest request);
}
