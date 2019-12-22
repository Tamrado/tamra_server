package com.webapp.timeline.sns.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webapp.timeline.sns.dto.ImageDto;
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
    private int postId;
    private String author;
    private String profile;
    private String content;
    private String showLevel;
    private String timestamp;
    @JsonProperty("files")
    private List<ImageDto> files;
    private int totalComment;
}
