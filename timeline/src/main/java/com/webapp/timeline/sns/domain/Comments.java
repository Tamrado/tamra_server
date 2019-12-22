package com.webapp.timeline.sns.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webapp.timeline.sns.web.json.CustomCommentDeserializer;
import com.webapp.timeline.sns.web.json.CustomCommentSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "comments")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = CustomCommentSerializer.class)
@JsonDeserialize(using = CustomCommentDeserializer.class)
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    @Column(name = "postId", nullable = false)
    private int postId;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "lastUpdate", nullable = false)
    private Timestamp lastUpdate;

    @Column(name = "deleted")
    private int deleted;

    public void setContent(String content) {
        this.content = content;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

}
