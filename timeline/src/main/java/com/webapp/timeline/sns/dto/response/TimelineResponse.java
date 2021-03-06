package com.webapp.timeline.sns.dto.response;

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
public class TimelineResponse {
    private int postId;

    @JsonProperty("profile")
    private ProfileResponse profile;

    private String content;

    private String showLevel;

    private String timestamp;

    private String dateString;

    @JsonProperty("files")
    private List<ImageResponse> files;

    @JsonProperty("tags")
    private List<LoggedInfo> tags;

    private int totalTag;

    private int totalComment;

    private int totalLike;

    @JsonProperty("islike")
    private String isLoggedInUserLikeIt;

    private String commentState;

    private int commentPage;

    @JsonProperty("commentList")
    private List<CommentResponse> commentList;

    @JsonProperty("isTrueComment")
    private boolean isTrueComment;

    private String menuVisible;

    private String showMenuVisible;

    private String modifyVisible;

    private String modifyText;
}
