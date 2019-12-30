package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {

    @Query(value = "SELECT t.receiver FROM Tags t WHERE postId = :postId")
    LinkedList<String> listTagListInPost(@Param("postId") int postId);

    @Modifying
    @Query(value = "UPDATE Tags t SET t.read = 1 " +
                    "WHERE t.postId = :postId AND t.receiver = :receiver AND t.read = 0")
    void markReadSingleTagAlarm(@Param("postId") int postId, @Param("receiver") String receiver);

    @Query(value = "SELECT t FROM Tags t " +
                    "WHERE t.receiver = :receiver AND t.alarm = 1 AND t.read = 0" +
                    "ORDER BY t.tagId DESC")
    LinkedList<Tags> fetchNotReadActivitiesByReceiver(@Param("receiver") String receiver);

    @Query(value = "SELECT t FROM Tags t " +
                    "WHERE t.receiver = :receiver AND t.alarm = 1 AND t.read = 1 " +
                    "ORDER BY t.tagId DESC")
    LinkedList<Tags> fetchAlreadyReadActivitiesByReceiver(@Param("receiver") String receiver);

    @Modifying
    @Query(value = "UPDATE Tags t SET t.read = 1 " +
                    "WHERE t.receiver = :receiver AND t.alarm = 1 AND t.read = 0")
    void makeActivitiesAllRead(@Param("receiver") String receiver);

    @Query(value = "SELECT COUNT(t.tagId) FROM Tags t " +
                    "WHERE t.receiver = :receiver AND t.alarm = 1 AND t.read = 0")
    Long countNewActivities(@Param("receiver") String receiver);
}
