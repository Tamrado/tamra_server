package com.webapp.timeline.sns.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private int postId;

    @JsonProperty("profile")
    private ProfileResponse profile;

    private String content;

    private String timestamp;
}
