package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    @Query(value = "SELECT myPosts FROM Posts myPosts WHERE myPosts.deleted = 0 AND myPosts.author = :author",
            nativeQuery = false)
    Page<Posts> listMyPostsByUser(Pageable pageable, @Param("author") String author);

    @Transactional
    @Query(value = "SELECT list FROM Posts list WHERE list.deleted = 0 AND list.showLevel = 1 AND list.author = :author",
            nativeQuery = false)
    Page<Posts> listPublicPostsByUser(Pageable pageable, @Param("author") String author);
}
