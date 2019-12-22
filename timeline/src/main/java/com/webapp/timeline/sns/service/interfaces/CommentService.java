package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.domain.Comments;
import com.webapp.timeline.sns.dto.response.CommentResponse;
import com.webapp.timeline.sns.dto.response.SnsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface CommentService {
    CommentResponse registerComment(int postId, Comments comment, HttpServletRequest request);

    Map<String, Integer> removeComment(long commentId, HttpServletRequest request);

    CommentResponse editComment(long commentId, Comments comment, HttpServletRequest request);

    SnsResponse<CommentResponse> listAllCommentsByPostId(Pageable pageable, int postId);
}
