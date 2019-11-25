package com.webapp.timeline.sns.domain;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name = "comments")
public class Comments {

    @EmbeddedId
    private CommentInfo info;

    @NotNull
    private String author;

    @NotNull
    private String content;

    @NotNull
    private Timestamp lastUpdate;

    private int delete;

    Comments() {
    }

    Comments(Builder builder) {
        this.info = builder.info;
        this.author = builder.author;
        this.content = builder.content;
        this.lastUpdate = builder.lastUpdate;
        this.delete = builder.delete;
    }

    public static class Builder {
        private CommentInfo info;
        private String author;
        private String content;
        private Timestamp lastUpdate;
        private int delete;

        public Builder info(CommentInfo info) {
            this.info = info;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder lastUpdate(Timestamp lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public Builder delete(int delete) {
            this.delete = delete;
            return this;
        }

        public Comments build() {
            return new Comments(this);
        }
    }

}
