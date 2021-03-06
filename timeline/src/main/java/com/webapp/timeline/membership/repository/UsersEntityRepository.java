package com.webapp.timeline.membership.repository;

import com.webapp.timeline.membership.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Repository
public interface UsersEntityRepository extends JpaRepository<Users,String> {

    @Query(value = "select u.userId from Users u where u.userId = :userId")
    String findIdByExistingId(@Param("userId") String userId);

    @Query(value = "select u from Users u where u.userId = :userId")
    Users findUsersById(@Param("userId") String userId);

    @Query(value = "select u.userId from Users u where u.email = :email")
    String findEmailByExistingEmail(@Param("email") String email);

    @Query(value = "select u.userId from Users u where u.phone = :phone")
    String findPhoneByExistingPhone(@Param("phone") String phone);

    @Query(value = "select u.userId from Users u where u.userId = :userId or " +
            "u.email = :email or u.phone = :phone")
    List<String> findOverlappedObject(@Param("userId") String userId, @Param("email") String email,
                                     @Param("phone") String phone);

    @Query(value = "select u.comment from Users u where u.userId = :userId")
    String findComment(@Param("userId")String userId);

    @Query(value = "select u.name from Users u where u.userId = :userId")
    String findNickname(@Param("userId") String userId);

    @Query(value = "select u.name as name, u.userId as userId, u.comment as comment,u.authority as authority from Users u where u.userId = :userId")
    Map<String,String> findUserInfo(@Param("userId")String userId);

    @Modifying
    @Query(value = "update Users u set u.gender = :gender, u.comment = :comment," +
            " u.address = :address ,u.name = :name, u.password = :password,u.phone = :phone," +
            "u.email = :email, u.birthday = :birthday where u.userId = :userId")
    void updateUser(@Param("gender") int gender, @Param("comment") String comment,
                    @Param("address") String address, @Param("name") String name,
                    @Param("email") String email, @Param("password") String password,
                    @Param("phone") String phone, @Param("userId") String userId,
                    @Param("birthday") Date birthday);

    @Modifying
    @Query(value = "update Users u set u.authority = :authority where u.userId = :userId")
    void updateUserAuthority(@Param("userId")String userId, @Param("authority")String authority);

    @Query("select u.userId from Users u where u.name like %:nickname% " +
            "and u.userId in(select fl.id.friendId from Followings fl where fl.id.userId = :uid and fl.isFollow = 1 and " +
            "fl.id.friendId in (select f.id.userId from Followers f where f.id.friendId = :uid and f.isFollow = 1))")
    List<String> findNameInFirstFriendList(@Param("uid")String uid,@Param("nickname") String nickname);

    @Query("select u.userId from Users u where u.name like %:nickname%" +
            " and u.userId in (select fl.id.userId from Followings fl where fl.id.friendId = :uid and fl.isFollow = 1 and " +
            "fl.id.userId in (select f.id.friendId from Followers f where f.id.userId = :uid and f.isFollow = 1))")
    List<String> findNameInSecondFriendList(@Param("uid")String uid,@Param("nickname") String nickname);


    @Query("select u.userId as userId, u.name as name, u.comment as comment from Users u where u.name like %:nickname% " +
            "and u.authority = 'ROLE_USER'")
    Page<Map<String,String>> findUsersBySearching(@Param("nickname")String nickname, Pageable pageable);

    @Modifying
    @Query("update Users u set u.isAlarm = 0 where u.userId = :uid")
    void updateUserOffAlarm (@Param("uid")String uid);

    @Modifying
    @Query("update Users u set u.isAlarm = 1 where u.userId = :uid")
    void updateUserOnAlarm (@Param("uid")String uid);

    @Modifying
    @Transactional
    @Query("update Users u set u.email = :email, u.comment = :comment where u.userId = :uid")
    void updateSecondSignUp(@Param("email")String email, @Param("comment") String comment,@Param("uid")String uid);

    @Query("select u.isAlarm from Users u where u.userId = :uid")
    Integer selectIsAlarmFromUid (@Param("uid")String uid);
}
