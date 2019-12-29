package com.webapp.timeline.sns.dto.response;

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
public class NewsfeedResponse {

    @JsonProperty("feed")
    private TimelineResponse feed;

    private String sender;

    private String category;

    @JsonProperty("comment")
    private List<CommentResponse> comment;
}
