package com.webapp.timeline.sns.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsfeedResponse {

    @JsonProperty("feed")
    private TimelineResponse feed;

    @JsonProperty("profileId")
    private String feedAuthorId;

    @JsonProperty("sender")
    private LinkedList<Map<String, String>> sender;

    private String category;

    private String message;

    @JsonProperty("comment")
    private List<CommentResponse> comment;
}
