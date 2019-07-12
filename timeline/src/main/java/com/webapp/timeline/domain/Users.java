package com.webapp.timeline.domain;

import java.sql.Date;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class Users {
    @EmbeddedId
    private MasterId id;

    private String password;
    private String name;
    private String phone;
    private String email;
    private Date birthday;
    private int gender;
    private String address;
    private String comment;
    private String profileUrl;
    private Date timestamp;
    private int group1;
    private int group2;
    private int group3;
    private int group4;

    public Users(MasterId id, String password, String name, String phone, String email, Date birthday, int gender,
                 String address, String comment, String profileUrl, Date timestamp, int group1, int group2, int group3, int group4) {
        this.id = id;
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

    public void setId(MasterId id) {
        this.id = id;
    }

    public MasterId getId() {
        return id;
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

    public String getName() {
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
