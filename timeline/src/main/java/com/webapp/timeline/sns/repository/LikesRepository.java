package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Likes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Query(value = "SELECT heart.likeId FROM Likes heart " +
                    "WHERE heart.postId = :#{#like.postId} AND heart.owner = :#{#like.owner}")
    Long isUserLikedPost(@Param("like") Likes like);

    @Query(value = "SELECT heart.owner FROM Likes heart WHERE heart.postId = :postId")
    Page<String> showLikesByPostId(Pageable pageable, @Param("postId") int postId);

    @Query(value = "SELECT COUNT(heart.likeId) FROM Likes heart WHERE heart.postId = :postId")
    Long countLikesByPostId(@Param("postId") int postId);

    @Query(value = "SELECT list.owner FROM Likes list " +
                    "WHERE list.postId = :postId ORDER BY list.likeId DESC",
            nativeQuery = false)
    List<String> getLikesByPostId(@Param("postId") int postId);
}
