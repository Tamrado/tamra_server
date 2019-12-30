package com.webapp.timeline.sns.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tags")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tagId;

    @Column(name = "postId", nullable = false)
    private int postId;

    @Column(name = "sender", nullable = false)
    private String sender;

    @Column(name = "receiver", nullable = false)
    private String receiver;

    @Column(name = "lastUpdate")
    private Timestamp timestamp;

    @Column(name = "alarm")
    private int alarm;

    @Column(name = "checked")
    private int read;
}
