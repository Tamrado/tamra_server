package com.webapp.timeline.event.dto;

import com.webapp.timeline.event.domain.Action;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class ActionResponse {
    private String message;
    private Date timestamp;
    private String link;

    private ActionResponse(String message, Date timestamp, String link) {
        this.message = message;
        this.timestamp = timestamp;
        this.link = link;
    }

    public static ActionResponse valueOf(MessageSourceAccessor messageSourceAccessor, Action action) {
        return new ActionResponse
                (messageSourceAccessor.getMessage(action.getCode(), action.getArguments()), action.getTimestamp(), action.getLink());
    }
}
