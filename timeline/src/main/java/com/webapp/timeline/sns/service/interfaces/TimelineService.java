package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.dto.response.SnsResponse;
import com.webapp.timeline.sns.dto.response.TimelineResponse;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

public interface TimelineService {

    SnsResponse<TimelineResponse> loadPostListByUser(String userId, Pageable pageable, HttpServletRequest request);

}
