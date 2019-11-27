package com.webapp.timeline.sns.service;

import com.webapp.timeline.sns.domain.CommentInfo;
import com.webapp.timeline.sns.domain.Comments;

import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    Comments registerComment(long postId, String content, HttpServletRequest request);

    Comments deleteComment(CommentInfo info);
}
