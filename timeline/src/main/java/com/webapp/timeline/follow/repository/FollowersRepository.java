package com.webapp.timeline.follow.repository;

import com.webapp.timeline.follow.domain.FollowerId;
import com.webapp.timeline.follow.domain.Followers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowersRepository extends JpaRepository<Followers, FollowerId> {
    @Query("select count(f) from Followers f where f.id.friendId = :uid and f.isFollow = 1")
    int findFollowNum(@Param("uid") String uid);

    @Query("select count(f) from Followers f where f.id.userId = :uid and f.isFollow = 1")
    int findFollowerNum(@Param("uid") String uid);

}
