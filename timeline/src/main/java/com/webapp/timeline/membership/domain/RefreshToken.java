package com.webapp.timeline.membership.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "refreshtoken")
@Data
public class RefreshToken {
    @Id
    private String uid;
    @Column(name="refresh_token")
    private String refreshToken;

    public RefreshToken(String uid, String refreshToken){
        this.uid = uid;
        this.refreshToken = refreshToken;
    }
}
