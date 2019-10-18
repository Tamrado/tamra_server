package com.webapp.timeline.sns.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webapp.timeline.sns.web.CustomPostDeserializer;
import com.webapp.timeline.sns.web.CustomPostSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;


//@DynamicInsert
//@DynamicUpdate
@Entity
@Table(name = "posts")
@JsonSerialize(using = CustomPostSerializer.class)
@JsonDeserialize(using = CustomPostDeserializer.class)
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    @NotNull
    private String userId;

    @NotNull(message="소식을 전달해주세요.")
    private String content;

    @Convert(converter = ConverterShowLevel.class)
    private String showLevel;

    @NotNull
    private Timestamp lastUpdate;

    public Posts(String userId, String content, String showLevel, Timestamp lastUpdate) {
        this.userId = userId;
        this.content = content;
        this.showLevel = showLevel;
        this.lastUpdate = lastUpdate;
    }

    public Posts() {}

    public int getPostId() {
        return postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setShowLevel(String showLevel) {
        this.showLevel = showLevel;
    }

    public String getShowLevel() {
        return showLevel;
    }

    public void setLastUpdate() {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        String now = LocalDateTime.now()
                                .atZone(zoneId)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        this.lastUpdate = Timestamp.valueOf(now);
        System.out.println(this.lastUpdate);
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public static PostsBuilder builder() {
        return new PostsBuilder();
    }

    public static class PostsBuilder {
        private long masterId;
        private String userId;
        private String content;
        private String showLevel;
        private Timestamp lastUpdate;

        PostsBuilder() {}

        public PostsBuilder masterId(long masterId) {
            this.masterId = masterId;
            return this;
        }

        public PostsBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public PostsBuilder content(String content) {
            this.content = content;
            return this;
        }

        public PostsBuilder showLevel(String showLevel) {
            this.showLevel = showLevel;
            return this;
        }

        public PostsBuilder lastUpdate(Timestamp lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public Posts build() {
            return new Posts(userId, content, showLevel, lastUpdate);
        }
    }

}
