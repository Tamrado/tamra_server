package com.webapp.timeline.sns.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class EventResponse {
    @JsonProperty("sender")
    private ProfileResponse sender;

    private String message;

    private String timestamp;

    private String dateString;

    private String link;

    private boolean isRead;
}
