package com.webapp.timeline.repository;

import com.webapp.timeline.domain.MasterId;
import com.webapp.timeline.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface UsersEntityRepository extends JpaRepository<Users, MasterId> {

    @Query(value = "select u from Users u where u.id = :id")
    Users findIdByExistingId(@Param("id") MasterId masterId);
    @Query(value = "select u from Users u where u.email = :email")
    Users findEmailByExistingEmail(@Param("email") String email);
    @Query(value = "select u from Users u where u.phone = :phone")
    Users findPhoneByExistingPhone(@Param("phone") String phone);


}

