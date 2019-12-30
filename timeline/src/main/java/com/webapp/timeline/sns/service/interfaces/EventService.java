package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.dto.response.EventResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.Map;

public interface EventService {

    LinkedList<EventResponse> fetchActivities(HttpServletRequest request);

    void makeEventsAllRead(HttpServletRequest request);

    Map<String, Long> countEvents(HttpServletRequest request);
}
