package com.webapp.timeline.repository;

import com.webapp.timeline.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query(value = "update Users u set u.group4 = :group4,u.group1 = :group1,u.group2 = :group2,u.group3 = :group3,u.gender = :gender, u.comment = :comment, u.address = :address ,u.name = :name, u.password = :password,u.phone = :phone,u.email = :email where u.userId = :userId")
    void updateUser(@Param("group4") int group4, @Param("group3") int group3, @Param("group2") int group2, @Param("group1") int group1, @Param("gender") int gender, @Param("comment") String comment, @Param("address") String address, @Param("name") String name, @Param("email") String email, @Param("password") String password, @Param("phone") String phone, @Param("userId") String userId);
}
