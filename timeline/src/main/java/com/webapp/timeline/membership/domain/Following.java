package com.webapp.timeline.membership.domain;


import java.sql.Date;
import javax.persistence.*;


@Entity
@Table(name = "following")
public class Following {

    @EmbeddedId
    private FollowingId id;

    private int isAccepted;

    private Date timestamp;


    public Following(FollowingId id, int isAccepted, Date timestamp) {
        this.id = id;
        this.isAccepted = isAccepted;
        this.timestamp = timestamp;
    }

    public Following() {}

    public void setId(FollowingId id) {
        this.id = id;
    }

    public FollowingId getId() {
        return id;
    }

    public void setIsAccepted(int isAccepted) {
        this.isAccepted = isAccepted;
    }

    public int getIsAccepted() {
        return isAccepted;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

