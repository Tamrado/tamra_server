package com.webapp.timeline.follow.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class FollowId implements Serializable {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "friend_id")
    private String friendId;

    public FollowId(String userId,String friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public FollowId() {}

    public boolean isEqual(Object object) {
        return (object instanceof FollowId);
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if(object instanceof FollowId) {
            FollowId followId = (FollowId) object;
            result = (followId.isEqual(this) &&
                    this.getFriendId().equals(followId.getFriendId()) &&
                    this.getUserId().equals(followId.getUserId()));
        }

        return result;
    }

    //hashCode 오버라이딩 하는 법 알아보기
    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getFriendId());
    }

}
