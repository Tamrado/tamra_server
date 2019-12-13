package com.webapp.timeline.follow.domain;

import com.webapp.timeline.follow.domain.FollowerId;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Data
@Table(name = "followers")
public class Followers {
    @EmbeddedId
    private FollowerId id;
    @Column(name="is_follow")
    private int isFollow;
    private Date date;

    public Followers(FollowerId id, int isFollow, Date date) {
        this.id = id;
        this.isFollow = isFollow;
        this.date = date;
    }

    public Followers() {}
}
