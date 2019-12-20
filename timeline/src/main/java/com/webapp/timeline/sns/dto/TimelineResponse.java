package com.webapp.timeline.sns.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineResponse {
    private String author;
    private String profile;
    private String content;
    private String showLevel;
    private String timestamp;
    @JsonProperty("files")
    private List<ImageResponse> files;
    private int totalComment;
}
