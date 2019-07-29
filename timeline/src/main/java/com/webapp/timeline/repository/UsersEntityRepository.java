package com.webapp.timeline.repository;

import com.webapp.timeline.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UsersEntityRepository extends JpaRepository<Users,String> {

    @Query(value = "select u from Users u where u.id = :id")
    Users findIdByExistingId(@Param("id") String id);
    @Query(value = "select u from Users u where u.email = :email")
    Users findEmailByExistingEmail(@Param("email") String email);
    @Query(value = "select u from Users u where u.phone = :phone")
    Users findPhoneByExistingPhone(@Param("phone") String phone);

}

