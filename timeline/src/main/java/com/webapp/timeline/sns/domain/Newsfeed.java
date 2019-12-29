package com.webapp.timeline.sns.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(name = "commentId")
    private long commentId;

}
