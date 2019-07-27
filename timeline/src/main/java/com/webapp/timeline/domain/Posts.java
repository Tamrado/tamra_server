package com.webapp.timeline.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

//@DynamicInsert
//@DynamicUpdate
@Entity
@Table(name = "posts")
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    private long masterId;

    private String userId;

    @NotNull(message="소식을 전달해주세요.")
    private String content;

    @Convert(converter = ConverterShowLevel.class)
    private String showLevel;
    
    private Timestamp lastUpdate;

    @Convert(converter = JpaConverterJson.class)
    private List<PhotoVO> photoUrl;

    public Posts(int masterId, String userId, String content, String showLevel, Timestamp lastUpdate, List<PhotoVO> photoUrl) {
        this.masterId = masterId;
        this.userId = userId;
        this.content = content;
        this.showLevel = showLevel;
        this.lastUpdate = lastUpdate;
        this.photoUrl = photoUrl;
    }

    public Posts() {}

    public int getPostId() {
        return postId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public long getMasterId() {
        return masterId;
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

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setPhotoUrl(List<PhotoVO> photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<PhotoVO> getPhotoUrl() {
        return photoUrl;
    }


}
