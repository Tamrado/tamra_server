package com.webapp.timeline.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
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

    public void setprofileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getprofileURL() {
        return profileURL;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
