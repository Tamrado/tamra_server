package com.webapp.timeline.sns.domain;


import javax.persistence.Embeddable;

@Embeddable
public class CommentInfo {
    private long postId;
    private long commentId;

    CommentInfo() {
    }

    public CommentInfo(Builder builder) {
        this.postId = builder.postId;
        this.commentId = builder.commentId;
    }

    public static class Builder {
        private long postId;
        private long commentId;

        public Builder() {
            postId = 0;
            commentId = 0;
        }

        public Builder postId(long postId) {
            this.postId = postId;
            return this;
        }

        public Builder commentId(long commentId) {
            this.commentId = commentId;
            return this;
        }

        public CommentInfo build() {
            return new CommentInfo(this);
        }
    }
}
