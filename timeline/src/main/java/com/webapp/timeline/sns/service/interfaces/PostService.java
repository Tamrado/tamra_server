package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.dto.request.PostRequest;
import com.webapp.timeline.sns.dto.response.NewsfeedResponse;
import com.webapp.timeline.sns.dto.response.TimelineResponse;
import com.webapp.timeline.sns.repository.NewsfeedRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


public interface PostService {
    Map<String, Integer> createEvent(PostRequest postRequest, HttpServletRequest request);
    
    Map<String, Integer> deletePost(int postId, HttpServletRequest request);

    Map<String, Integer> updatePost(int postId, PostRequest postRequest, HttpServletRequest request);

    NewsfeedResponse getOnePostByPostId(int postId, HttpServletRequest request);
}
