package com.webapp.timeline.sns.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "newsfeed")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Newsfeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "postId", nullable = false)
    private int postId;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "sender")
    private String sender;

    @Column(name = "receiver", nullable = false)
    private String receiver;

    @Column(name = "lastUpdate")
    private Timestamp lastUpdate;

    @Column(name = "commentId", nullable = true)
    private long commentId;

}
