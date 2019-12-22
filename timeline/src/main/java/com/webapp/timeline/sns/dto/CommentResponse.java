package com.webapp.timeline.sns.dto;

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
    private String author;
    private String profile;
    private String content;
    private String timestamp;
}
