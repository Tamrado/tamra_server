package com.webapp.timeline.repository;

import com.webapp.timeline.domain.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImagesRepository extends JpaRepository<Profiles,Long> {

}
