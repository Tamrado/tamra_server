package com.webapp.timeline.domain;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Null;
import java.util.List;

@Entity
@Table(name = "images")
public class PostImages {
    @Id
    private long postId;

    @Null
    @Convert(converter = JpaConverterJson.class)
    private List<PhotoVO> photoUrl;

    PostImages() {}

    public PostImages(long postId, List<PhotoVO> photoUrl) {
        this.postId = postId;
        this.photoUrl = photoUrl;
    }

    public long getPostId() {
        return this.postId;
    }

    public List<PhotoVO> getPhotoUrl() {
        return this.photoUrl;
    }
}
