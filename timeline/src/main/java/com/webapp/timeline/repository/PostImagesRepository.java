package com.webapp.timeline.repository;

import com.webapp.timeline.domain.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImagesRepository extends JpaRepository<PostImages, String> {
}
