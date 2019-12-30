package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {

    @Query(value = "SELECT t.userId FROM Tags t WHERE postId = :postId")
    LinkedList<String> listTagListInPost(@Param("postId") int postId);

}
