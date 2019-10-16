package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImagesRepository extends JpaRepository<PostImages, String> {
}
