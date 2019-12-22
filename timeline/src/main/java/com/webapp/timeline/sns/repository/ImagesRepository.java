package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {

    @Query(value = "SELECT i FROM Images i " +
                "WHERE i.postId = :postId AND i.deleted = 0")
    List<Images> listImageListInPost(@Param("postId") int postId);

    @Modifying
    @Query(value = "UPDATE Images i SET i.deleted = 1 " +
                "WHERE i.postId = :postId AND i.deleted = 0")
    Integer markDeleteByPostId(@Param("postId") int postId);

}
