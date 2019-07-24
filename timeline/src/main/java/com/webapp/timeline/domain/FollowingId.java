package com.webapp.timeline.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FollowingId implements Serializable {

    private String userId;
    private Long masterId;
    private String followId;

    public FollowingId(String userId,Long masterId,String followId) {
        this.userId = userId;
        this.masterId = masterId;
        this.followId = followId;
    }

    public FollowingId() {}

    public String getuserId() {
        return userId;
    }

    public Long getMasterId(){ return masterId;}

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
                    this.getFollowId().equals(followingId.getFollowId()) &&
                    this.getuserId().equals(followingId.getuserId()));
        }

        return result;
    }

    //hashCode 오버라이딩 하는 법 알아보기
    @Override
    public int hashCode() {
        return Objects.hash(getuserId(),getMasterId(), getFollowId());
    }

}
