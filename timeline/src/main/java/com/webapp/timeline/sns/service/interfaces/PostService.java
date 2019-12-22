package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.dto.request.EventRequest;
import com.webapp.timeline.sns.dto.response.TimelineResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


public interface PostService {
    Map<String, Integer> createEvent(EventRequest eventRequest, HttpServletRequest request);
    
    Map<String, Integer> deletePost(int postId, HttpServletRequest request);

    Map<String, Integer> updatePost(int postId, EventRequest eventRequest, HttpServletRequest request);

    TimelineResponse getOnePostByPostId(int postId, HttpServletRequest request);
}
