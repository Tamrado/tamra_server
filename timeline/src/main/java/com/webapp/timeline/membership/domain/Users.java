package com.webapp.timeline.membership.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webapp.timeline.membership.domain.deserializer.UsersDeserializer;
import com.webapp.timeline.membership.domain.serializer.UsersSerializer;
import lombok.Data;
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
@Data
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
    private Integer gender;
    private String address;
    private String comment;
    private Date timestamp;
    private int isAlarm;

    public Users(String userId, String password, String name, String phone, String email, Date birthday, Integer gender,
                 String address, String comment) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.address = address;
        this.comment = comment;
    }

    public Users() {}

    @Override
    //특수문자가 들어간 아이디는 관리자, 안들어간 아이디는 사용자
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
        auth.add(new SimpleGrantedAuthority(authority));
        return auth;
    }

    public String getAuthority(){
        return authority;
    }
    public void setAuthority(){
       String pattern = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$";
       Boolean result = Pattern.matches(pattern,this.getUserId());
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

    public String getUsername() {
        return name;
    }
}
