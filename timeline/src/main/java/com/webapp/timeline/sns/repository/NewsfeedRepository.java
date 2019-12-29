package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Newsfeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsfeedRepository extends JpaRepository<Newsfeed, Long> {

    @Query(value = "SELECT list FROM Newsfeed list WHERE list.receiver = :receiver",
            nativeQuery = false)
    Page<Newsfeed> getNewsfeedByReceiver(Pageable pageable, @Param("receiver") String receiver);

    @Modifying
    @Query(value = "DELETE FROM Newsfeed list WHERE list.postId = :postId",
            nativeQuery = false)
    void deleteNewsfeedByPostId(@Param("postId") int postId);

    @Modifying
    @Query(value = "DELETE FROM Newsfeed list WHERE list.sender = :sender AND list.commentId = :commentId",
            nativeQuery = false)
    void deleteNewsfeedByComment(@Param("sender") String sender, @Param("commentId") long commentId);

    @Modifying
    @Query(value = "DELETE FROM Newsfeed list " +
                   "WHERE list.postId = :postId AND list.sender = :sender AND list.category = :category",
            nativeQuery = false)
    void deleteNewsfeedByLike(@Param("postId") int postId, @Param("sender") String sender, @Param("category") String category);
}
