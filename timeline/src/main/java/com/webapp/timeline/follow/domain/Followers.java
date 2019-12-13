package com.webapp.timeline.follow.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
@Table(name = "followers")
public class Followers {
    @EmbeddedId
    private FollowId id;
    @Column(name = "is_follow")
    private int isFollow;
    @Basic(optional = false)
    @Column(insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public Followers(FollowId id, int isFollow) {
        this.id = id;
        this.isFollow = isFollow;
    }

    public Followers() {}
}
