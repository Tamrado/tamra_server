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
    @Query(value = "UPDATE Newsfeed news SET news.frequency = news.frequency+1, news.lastUpdate = :#{#news.lastUpdate} " +
                    "WHERE news.postId = :#{#news.postId} AND news.category = :#{#news.category} " +
                    "AND news.receiver = :#{#news.receiver} AND news.frequency >= 0")
    Integer deliverLikeOrCommentNews(@Param("news") Newsfeed news);

    @Modifying
    @Query(value = "UPDATE Newsfeed news SET news.frequency = news.frequency-1 " +
            "WHERE news.postId = :#{#news.postId} AND news.category = :#{#news.category} AND news.receiver = :#{#news.receiver}")
    Integer withdrawLikeOrCommentNews(@Param("news") Newsfeed news);

    @Query(value = "SELECT list FROM Newsfeed list " +
                    "WHERE list.receiver = :receiver AND list.frequency > 0 ORDER BY list.lastUpdate DESC",
            nativeQuery = false)
    Page<Newsfeed> getNewsfeedByReceiver(Pageable pageable, @Param("receiver") String receiver);

    @Modifying
    @Query(value = "DELETE FROM Newsfeed list WHERE list.postId = :postId",
            nativeQuery = false)
    void deleteNewsfeedByPostId(@Param("postId") int postId);

}
