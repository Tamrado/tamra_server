package com.webapp.timeline.sns.dto.request;

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
public class PostRequest {
    private String content;
    private String showLevel;
    @JsonProperty("files")
    private List<ImageDto> files;
}
