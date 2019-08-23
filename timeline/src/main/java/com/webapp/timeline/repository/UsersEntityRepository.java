package com.webapp.timeline.repository;

import com.webapp.timeline.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UsersEntityRepository extends JpaRepository<Users,Long> {

    @Query(value = "select u from Users u where u.userId = :userId")
    Users findIdByExistingId(@Param("userId") String userId);
    @Query(value = "select u from Users u where u.email = :email")
    Users findEmailByExistingEmail(@Param("email") String email);
    @Query(value = "select u from Users u where u.phone = :phone")
    Users findPhoneByExistingPhone(@Param("phone") String phone);
    @Query(value = "select u from Users u where u.userId = :userId or u.email = :email or u.phone = :phone")
    List<Users> findOverlappedObject(@Param("userId") String userId, @Param("email") String email, @Param("phone") String phone);

}

