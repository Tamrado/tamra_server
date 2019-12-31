package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.dto.response.SnsResponse;
import org.springframework.data.domain.Page;

import java.util.LinkedList;

public interface SnsResponseHelper<T, M> {

    T makeSingleResponse(M item, String loggedIn);

    default SnsResponse<T> makeSnsResponse(Page<M> pagedList, String loggedIn) {
        LinkedList<T> eventList = new LinkedList<>();

        pagedList.forEach(item -> {
            eventList.add(makeSingleResponse(item, loggedIn));
        });

        return SnsResponse.<T>builder()
                            .objectSet(eventList)
                            .first(pagedList.isFirst())
                            .last(pagedList.isLast())
                            .build();
    }
}
