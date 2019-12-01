package com.webapp.timeline.sns.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webapp.timeline.sns.web.json.CustomPostDeserializer;
import com.webapp.timeline.sns.web.json.CustomPostSerializer;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "posts")
@JsonSerialize(using = CustomPostSerializer.class)
@JsonDeserialize(using = CustomPostDeserializer.class)
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "content", nullable = false)
    private String content;

    @Convert(converter = ConverterShowLevel.class)
    @Column(name = "showLevel", nullable = false)
    private String showLevel;

    @Column(name = "lastUpdate", nullable = false)
    private Timestamp lastUpdate;

    public Posts() {
    }

    Posts(Builder builder) {
        this.postId = builder.postId;
        this.author = builder.author;
        this.content = builder.content;
        this.showLevel = builder.showLevel;
        this.lastUpdate = builder.lastUpdate;
    }

    public int getPostId() {
        return this.postId;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getContent() {
        return this.content;
    }

    public String getShowLevel() {
        return this.showLevel;
    }

    public Timestamp getLastUpdate() {
        return this.lastUpdate;
    }

    public static class Builder {
        private int postId;
        private String author;
        private String content;
        private String showLevel;
        private Timestamp lastUpdate;

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder showLevel(String showLevel) {
            this.showLevel = showLevel;
            return this;
        }

        public Builder lastUpdate(Timestamp lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public Posts build() {
            return new Posts(this);
        }
    }
}
