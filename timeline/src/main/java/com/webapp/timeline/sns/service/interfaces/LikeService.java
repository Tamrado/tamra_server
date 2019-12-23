package com.webapp.timeline.sns.service.interfaces;


import javax.servlet.http.HttpServletRequest;

public interface LikeService {
    void clickHeart(int postId, HttpServletRequest request);

    void cancelHeart(int postId, HttpServletRequest request);
}
