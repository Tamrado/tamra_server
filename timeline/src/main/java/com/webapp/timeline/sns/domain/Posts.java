package com.webapp.timeline.sns.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "posts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    @Column(name = "author", nullable = false)
    private String author;

    @Setter
    @Column(name = "content", nullable = false)
    private String content;

    @Setter
    @Convert(converter = ConverterShowLevel.class)
    @Column(name = "showLevel", nullable = false)
    private String showLevel;

    @Setter
    @Column(name = "lastUpdate", nullable = false)
    private Timestamp lastUpdate;

    @Setter
    @Column(name = "deleted")
    private int deleted;

}
