package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Query(value = "SELECT heart.likeId FROM Likes heart " +
                    "WHERE heart.postId = :#{#like.postId} AND heart.owner = :#{#like.owner}")
    Long isUserLikedPost(@Param("like") Likes like);
}
