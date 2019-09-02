package com.webapp.timeline.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {
    private String idForEncode;
    Logger log = LoggerFactory.getLogger(this.getClass());
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
        return BCrypt.hashpw((String)rawPassword, BCrypt.gensalt(10));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword)
    {
        return BCrypt.checkpw((String)rawPassword, encodedPassword);
    }

}
