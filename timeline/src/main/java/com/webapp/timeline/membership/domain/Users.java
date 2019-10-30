package com.webapp.timeline.membership.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webapp.timeline.membership.web.deserializer.UsersDeserializer;
import com.webapp.timeline.membership.web.serializer.UsersSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import javax.persistence.*;

@Entity
@Table(name = "users")
@JsonDeserialize(using = UsersDeserializer.class)
@JsonSerialize(using = UsersSerializer.class)
public class Users implements UserDetails {
    @Id
    private String userId;
    private String password;
    private String authority;
    private String name;
    private String phone;
    private String email;
    private Date birthday;
    private int gender;
    private String address;
    private String comment;
    private Date timestamp;

    public Users(String userId, String password, String name, String phone, String email, Date birthday, int gender,
                 String address, String comment, Date timestamp, int group1, int group2, int group3, int group4) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.address = address;
        this.comment = comment;
        this.timestamp = timestamp;

    }

    public Users() {}

    @Override
    //특수문자가 들어간 아이디는 관리자, 안들어간 아이디는 사용자
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
        auth.add(new SimpleGrantedAuthority(authority));
        return auth;
    }

    public void setAuthorities(){
       String pattern = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$";
       Boolean result = Pattern.matches(pattern,this.getId());
        if(result)
            authority = "ROLE_USER";

        else
            authority = "ROLE_ADMIN";
    }
    public void setAuthoritytoInactive(){
        authority = "ROLE_INACTIVEUSER";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getId() {
        return userId;
    }

    public void setId(String userId){
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getGender() {
        return gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }


}
