package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Posts p SET p.deleted = :#{#post.deleted} " +
                    "WHERE p.postId = :#{#post.postId}",
            nativeQuery = false)
    Integer markDeleteByPostId(@Param("post") Posts post);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Posts p " +
                "SET p.content = :#{#post.content}, p.showLevel = :#{#post.showLevel}, p.lastUpdate = :#{#post.lastUpdate} " +
                "WHERE p.postId = :#{#post.postId}",
            nativeQuery = false)
    Integer updatePostByPostId(@Param("post") Posts post);
}
