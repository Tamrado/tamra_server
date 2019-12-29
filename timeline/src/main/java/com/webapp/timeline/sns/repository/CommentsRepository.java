package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {

    @Modifying
    @Query(value = "UPDATE Comments item SET item.deleted = 1 " +
                    "WHERE item.postId = :postId AND item.deleted = 0")
    Integer markDeleteByPostId(@Param("postId") int postId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Comments item SET item.deleted = :#{#comment.deleted} " +
                    "WHERE item.commentId = :#{#comment.commentId} AND item.deleted = 0",
            nativeQuery = false)
    Integer markDeleteByCommentId(@Param("comment") Comments comment);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Comments item " +
                    "SET item.content = :#{#comment.content}, item.lastUpdate = :#{#comment.lastUpdate} " +
                    "WHERE item.commentId = :#{#comment.commentId} AND item.deleted = 0",
            nativeQuery = false)
    Integer editCommentByCommentId(@Param("comment") Comments comment);

    @Transactional
    @Query(value = "SELECT list FROM Comments list WHERE list.deleted = 0 AND list.postId = :postId",
            nativeQuery = false)
    Page<Comments> listValidCommentsByPostId(Pageable pageable, @Param("postId") int postId);

    @Query(value = "SELECT COUNT(list.commentId) FROM Comments list WHERE list.deleted = 0 AND list.postId = :postId")
    Long countCommentsByPostId(@Param("postId") int postId);

    @Transactional
    @Query(value = "SELECT list FROM Comments list WHERE list.deleted = 0 AND list.postId = :postId",
            nativeQuery = false)
    List<Comments> getCommentsByPostId(@Param("postId") int postId);
}
