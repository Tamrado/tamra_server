package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.dto.response.SnsResponse;
import com.webapp.timeline.sns.dto.response.TimelineResponse;
import org.springframework.data.domain.Page;

import java.util.LinkedList;

public interface TimelineResponseHelper {

    TimelineResponse makeSingleResponse(Posts post);

    default SnsResponse<TimelineResponse> makeSnsResponse(Page<Posts> pagedList) {
        LinkedList<TimelineResponse> eventList = new LinkedList<>();

        pagedList.forEach(item -> {
            eventList.add(makeSingleResponse(item));
        });

        return SnsResponse.<TimelineResponse>builder()
                            .objectSet(eventList)
                            .first(pagedList.isFirst())
                            .last(pagedList.isLast())
                            .build();
    }
}
