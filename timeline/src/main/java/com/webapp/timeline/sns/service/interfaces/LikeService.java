package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.dto.response.LikeResponse;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

public interface LikeService {
    void clickHeart(int postId, HttpServletRequest request);

    void cancelHeart(int postId, HttpServletRequest request);

    LikeResponse showLikes(Pageable pageable, int postId);
}
