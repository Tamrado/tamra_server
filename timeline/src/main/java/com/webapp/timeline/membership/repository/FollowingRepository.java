package com.webapp.timeline.membership.repository;


import com.webapp.timeline.membership.domain.Following;
import com.webapp.timeline.membership.domain.FollowingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FollowingRepository extends JpaRepository<Following, FollowingId> {

}