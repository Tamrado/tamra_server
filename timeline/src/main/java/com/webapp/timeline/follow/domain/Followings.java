package com.webapp.timeline.follow.domain;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "followings")
public class Followings {

    @EmbeddedId
    private FollowId id;
    @Column(name = "is_follow")
    private int isFollow;
    @Basic(optional = false)
    @Column(insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public Followings(FollowId id, int isFollow) {
        this.id = id;
        this.isFollow = isFollow;
    }

    public Followings() {}



}

