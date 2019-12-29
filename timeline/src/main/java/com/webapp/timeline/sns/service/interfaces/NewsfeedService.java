package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.dto.response.NewsfeedResponse;
import com.webapp.timeline.sns.dto.response.SnsResponse;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

public interface NewsfeedService {

    SnsResponse<NewsfeedResponse> dispatch(Pageable pageable, HttpServletRequest request);
}
