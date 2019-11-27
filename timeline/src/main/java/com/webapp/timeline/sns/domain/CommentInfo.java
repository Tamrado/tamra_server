package com.webapp.timeline.sns.domain;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Embeddable
public class CommentInfo {

    @Column(name = "commentId", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    @Column(name = "postId", nullable = false)
    private long postId;

    CommentInfo() {
    }

    public CommentInfo(Builder builder) {
        this.commentId = builder.commentId;
        this.postId = builder.postId;
    }

    public static class Builder {
        private long commentId;
        private long postId;

        public Builder() {
            commentId = 0;
            postId = 0;
        }

        public Builder commentId(long commentId) {
            this.commentId = commentId;
            return this;
        }

        public Builder postId(long postId) {
            this.postId = postId;
            return this;
        }

        public CommentInfo build() {
            return new CommentInfo(this);
        }
    }
}
