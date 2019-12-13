package com.webapp.timeline.membership.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "profiles")
public class Profiles {

    @Id
    private String id;
    private String profileURL;

    public Profiles(String id, String profileURL) {
        this.id = id;
        this.profileURL = profileURL;
    }

    public Profiles() {}
}
