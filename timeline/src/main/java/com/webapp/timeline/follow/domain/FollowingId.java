package com.webapp.timeline.follow.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class FollowingId implements Serializable {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "friend_id")
    private String friendId;

    public FollowingId(String userId,String friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public FollowingId() {}

    public boolean isEqual(Object object) {
        return (object instanceof FollowingId);
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if(object instanceof FollowingId) {
            FollowingId followingId = (FollowingId) object;
            result = (followingId.isEqual(this) &&
                    this.getFriendId().equals(followingId.getFriendId()) &&
                    this.getUserId().equals(followingId.getUserId()));
        }

        return result;
    }

    //hashCode 오버라이딩 하는 법 알아보기
    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getFriendId());
    }

}
