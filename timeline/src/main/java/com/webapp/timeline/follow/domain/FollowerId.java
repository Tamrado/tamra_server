package com.webapp.timeline.follow.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class FollowerId implements Serializable  {

    @Column(name = "user_id")
    private String userId;
    @Column(name = "friend_id")
    private String friendId;

    public FollowerId(String userId, String friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public FollowerId() {}

    public boolean isEqual(Object object) {
        return (object instanceof FollowerId);
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if(object instanceof FollowerId) {
            FollowerId followerId = (FollowerId) object;
            result = (followerId.isEqual(this) &&
                    this.getFriendId().equals(followerId.getFriendId()) &&
                    this.getUserId().equals(followerId.getUserId()));
        }

        return result;
    }

    //hashCode 오버라이딩 하는 법 알아보기
    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getFriendId());
    }

}
