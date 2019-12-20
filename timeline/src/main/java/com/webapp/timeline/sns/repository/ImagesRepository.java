package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {

    @Query(value = "SELECT images FROM Images images WHERE images.postId = :postId")
    List<Images> listImageListInPost(@Param("postId") int postId);
}
