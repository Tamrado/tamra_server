package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.dto.PagingResponse;
import com.webapp.timeline.sns.dto.TimelineResponse;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

public interface TimelineService {

    PagingResponse<TimelineResponse> loadPostListByUser(String userId, Pageable pageable, HttpServletRequest request);

}
