package com.webapp.timeline.domain;


import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class PostId implements Serializable {

    private MasterId id;
    private int postId;

    public PostId(MasterId id, int postId) {
        this.id = id;
        this.postId = postId;
    }

    public PostId() {}

    public MasterId getMasterId() {
        return id;
    }

    public int getPostId() {
        return postId;
    }

    public boolean isEqual(Object object) {
        return (object instanceof PostId);
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if(object instanceof PostId) {
            PostId postingId = (PostId) object;
            result = (postingId.isEqual(this) &&
                    this.getMasterId() == postingId.getMasterId() &&
                    this.getPostId() == postingId.getPostId());
        }

        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMasterId(), getPostId());
    }
}
