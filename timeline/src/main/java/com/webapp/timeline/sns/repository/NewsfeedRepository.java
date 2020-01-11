package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Newsfeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsfeedRepository extends JpaRepository<Newsfeed, Long> {

    @Modifying
    @Query(value = "DELETE FROM Newsfeed list " +
            "WHERE list.postId = :#{#news.postId} AND list.category = :#{#news.category} AND list.sender = :#{#news.sender}")
    void deleteNewsfeedOfLike(@Param("news") Newsfeed news);

    @Modifying
    @Query(value = "DELETE FROM Newsfeed list " +
            "WHERE list.category = :#{#news.category} AND list.commentId = :#{#news.commentId}")
    void deleteNewsfeedOfComment(@Param("news") Newsfeed news);

    @Query(value = "SELECT list FROM Newsfeed list " +
            "WHERE list.id IN (SELECT MAX(f.id) FROM Newsfeed f WHERE f.receiver = :receiver GROUP BY f.postId) " +
            "ORDER BY list.lastUpdate DESC",
            nativeQuery = false)
    Page<Newsfeed> getNewsfeedByReceiver(Pageable pageable, @Param("receiver") String receiver);

    @Modifying
    @Query(value = "DELETE FROM Newsfeed list WHERE list.postId = :postId",
            nativeQuery = false)
    void deleteNewsfeedByPostId(@Param("postId") int postId);

    @Modifying
    @Query(value = "DELETE FROM Newsfeed list WHERE list.sender = :sender AND list.receiver = :receiver")
    void deleteNewsfeedWhenUnfollow(@Param("sender") String sender, @Param("receiver") String receiver);
}
