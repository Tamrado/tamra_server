package com.webapp.timeline.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FollowingId implements Serializable {

    private MasterId id;
    private String followId;

    public FollowingId(MasterId id, String followId) {
        this.id = id;
        this.followId = followId;
    }

    public FollowingId() {}

    public MasterId getMasterId() {
        return id;
    }

    public String getFollowId() {
        return followId;
    }

    public boolean isEqual(Object object) {
        return (object instanceof FollowingId);
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if(object instanceof FollowingId) {
            FollowingId followingId = (FollowingId) object;
            result = (followingId.isEqual(this) &&
                    this.getMasterId() == followingId.getMasterId() &&
                    this.getFollowId().equals(followingId.getFollowId()));
        }

        return result;
    }

    //hashCode 오버라이딩 하는 법 알아보기
    @Override
    public int hashCode() {
        return Objects.hash(getMasterId(), getFollowId());
    }

}
