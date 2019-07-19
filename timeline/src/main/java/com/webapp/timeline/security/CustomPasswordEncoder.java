package com.webapp.timeline.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class CustomPasswordEncoder implements PasswordEncoder {
    private String idForEncode;
    private Map<String, PasswordEncoder> encoders;
    private PasswordEncoder passwordEncoder;

    public CustomPasswordEncoder(){
        encoders = new HashMap<>();
        idForEncode = "bcrypt";
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        this.passwordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
    }
    @Override
    public String encode(CharSequence rawPassword)
    {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword)
    {
        return encodedPassword.equals(encode(rawPassword));
    }

}
