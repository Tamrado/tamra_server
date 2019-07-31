package com.webapp.timeline.domain;

import com.google.common.primitives.UnsignedInts;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class Users implements UserDetails {
    private String userId;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long masterId;
    private String password;
    private String authority;
    private String name;
    private String phone;
    private String email;
    private Date birthday;
    private int gender;
    private String address;
    private String comment;
    private String profileUrl;
    private Date timestamp;
    private ArrayList<GrantedAuthority> auth;
    private int group1;
    private int group2;
    private int group3;
    private int group4;
    private String pattern;
    private Boolean result;

    public Users(String userId, String password, String name, String phone, String email, Date birthday, int gender,
                 String address, String comment, String profileUrl, Date timestamp, int group1, int group2, int group3, int group4) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.address = address;
        this.comment = comment;
        this.profileUrl = profileUrl;
        this.timestamp = timestamp;
        this.group1 = group1;
        this.group2 = group2;
        this.group3 = group3;
        this.group4 = group4;
    }

    public Users() {}

    @Override
    //특수문자가 들어간 아이디는 관리자, 안들어간 아이디는 사용자
    public Collection<? extends GrantedAuthority> getAuthorities() {
        auth = new ArrayList<GrantedAuthority>();
        auth.add(new SimpleGrantedAuthority(authority));
        return auth;
    }

    public void setAuthorities(){
        pattern = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$";
        result = Pattern.matches(pattern,this.getId());
        if(result)
            authority = "ROLE_USER";

        else
            authority = "ROLE_ADMIN";
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

    public Long getMasterId(){ return masterId; }

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

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setGroup1(int group1) {
        this.group1 = group1;
    }

    public int getGroup1() {
        return group1;
    }

    public void setGroup2(int group2) {
        this.group2 = group2;
    }

    public int getGroup2() {
        return group2;
    }

    public void setGroup3(int group3) {
        this.group3 = group3;
    }

    public int getGroup3() {
        return group3;
    }

    public void setGroup4(int group4) {
        this.group4 = group4;
    }

    public int getGroup4() {
        return group4;
    }

}
