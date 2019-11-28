package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Comments item SET item.deleted = :#{#comment.deleted} " +
                    "WHERE item.commentId = :#{#comment.commentId}",
            nativeQuery = false)
    Integer markDeleteByCommentId(@Param("comment") Comments comment);
}
