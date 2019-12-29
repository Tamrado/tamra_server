package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.dto.response.SnsResponse;
import org.springframework.data.domain.Page;

import java.util.LinkedList;

public interface SnsResponseHelper<T, M> {

    T makeSingleResponse(M item);

    default SnsResponse<T> makeSnsResponse(Page<M> pagedList) {
        LinkedList<T> eventList = new LinkedList<>();

        pagedList.forEach(item -> {
            eventList.add(makeSingleResponse(item));
        });

        return SnsResponse.<T>builder()
                            .objectSet(eventList)
                            .first(pagedList.isFirst())
                            .last(pagedList.isLast())
                            .build();
    }
}
