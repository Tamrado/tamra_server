package com.webapp.timeline.follow.domain;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Data
@Table(name = "followings")
public class Followings {

    @EmbeddedId
    private FollowingId id;
    @Column(name="is_follow")
    private int isFollow;
    private Date date;

    public Followings(FollowingId id, int isFollow, Date date) {
        this.id = id;
        this.isFollow = isFollow;
        this.date = date;
    }

    public Followings() {}



}

