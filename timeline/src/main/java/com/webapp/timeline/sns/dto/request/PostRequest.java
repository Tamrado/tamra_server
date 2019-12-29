package com.webapp.timeline.sns.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private String content;

    private String showLevel;

    @JsonProperty("tags")
    private List<LoggedInfo> tags;
}
