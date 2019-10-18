package com.webapp.timeline.membership.repository;

import com.webapp.timeline.membership.domain.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImagesRepository extends JpaRepository<Profiles,Long> {

}
