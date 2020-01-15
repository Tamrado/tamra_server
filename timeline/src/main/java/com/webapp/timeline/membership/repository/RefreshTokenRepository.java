package com.webapp.timeline.membership.repository;

import com.webapp.timeline.membership.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {

    @Modifying
    @Query(value = "update RefreshToken r set r.uid = :uid, r.refreshToken = :refreshToken where r.uid = :uid")
    void updateRefreshToken(@Param("uid") String uid, @Param("refreshToken") String refreshToken);
}
