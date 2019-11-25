package com.webapp.timeline.membership.repository;

import com.webapp.timeline.membership.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UsersEntityRepository extends JpaRepository<Users,String> {

    @Query(value = "select u from Users u where u.userId = :userId")
    Users findIdByExistingId(@Param("userId") String userId);

    @Query(value = "select u from Users u where u.email = :email")
    Users findEmailByExistingEmail(@Param("email") String email);

    @Query(value = "select u from Users u where u.phone = :phone")
    Users findPhoneByExistingPhone(@Param("phone") String phone);

    @Query(value = "select u from Users u where u.userId = :userId or u.email = :email or u.phone = :phone")
    List<Users> findOverlappedObject(@Param("userId") String userId, @Param("email") String email, @Param("phone") String phone);

    @Modifying
    @Query(value = "update Users u set u.gender = :gender, u.comment = :comment, u.address = :address ,u.name = :name, u.password = :password,u.phone = :phone,u.email = :email where u.userId = :userId")
    void updateUser(@Param("gender") int gender, @Param("comment") String comment, @Param("address") String address, @Param("name") String name, @Param("email") String email, @Param("password") String password, @Param("phone") String phone, @Param("userId") String userId);

    @Modifying
    @Query(value = "update Users u set u.authority = :authority where u.userId = :userId")
    void updateUserAuthority(@Param("userId")String userId, @Param("authority")String authority);
}
