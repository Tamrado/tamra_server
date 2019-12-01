package com.webapp.timeline.sns.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webapp.timeline.sns.web.json.CustomCommentDeserializer;
import com.webapp.timeline.sns.web.json.CustomCommentSerializer;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "comments")
@JsonSerialize(using = CustomCommentSerializer.class)
@JsonDeserialize(using = CustomCommentDeserializer.class)
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    @Column(name = "postId", nullable = false)
    private long postId;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "lastUpdate", nullable = false)
    private Timestamp lastUpdate;

    @Column(name = "deleted")
    private int deleted;

    public Comments() {
    }

    Comments(Builder builder) {
        this.postId = builder.postId;
        this.author = builder.author;
        this.content = builder.content;
        this.lastUpdate = builder.lastUpdate;
        this.deleted = builder.deleted;
    }

    public long getCommentId() {
        return this.commentId;
    }

    public long getPostId() {
        return this.postId;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Timestamp getLastUpdate() {
        return this.lastUpdate;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getDeleted() {
        return this.deleted;
    }

    public static class Builder {
        private long postId;
        private String author;
        private String content;
        private Timestamp lastUpdate;
        private int deleted;

        public Builder postId(long postId) {
            this.postId = postId;
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

        public Builder deleted(int deleted) {
            this.deleted = deleted;
            return this;
        }

        public Comments build() {
            return new Comments(this);
        }
    }

}
