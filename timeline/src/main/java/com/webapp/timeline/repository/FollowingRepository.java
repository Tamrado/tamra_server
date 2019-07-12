package com.webapp.timeline.repository;

import com.webapp.timeline.domain.Following;
import com.webapp.timeline.domain.FollowingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FollowingRepository extends JpaRepository<Following, FollowingId> {

}