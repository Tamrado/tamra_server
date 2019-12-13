package com.webapp.timeline.membership.repository;

import com.webapp.timeline.membership.domain.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImagesRepository extends JpaRepository<Profiles,String> {
    @Query(value = "select p from Profiles p where p.id = :id")
    Profiles findImageURLById(@Param("id") String id);

    @Modifying
    @Query(value = "update Profiles p set p.id = :id, p.profileURL = :profileURL where p.id = :id")
    void updateProfile(@Param("id") String id, @Param("profileURL") String profileURL);

    @Query(value = "select count(id) from Profiles p where p.id = :id")
    int selectProfileNum(@Param("id") String id);
}
