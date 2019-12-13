package com.webapp.timeline.membership.domain.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.webapp.timeline.membership.domain.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class UsersDeserializer extends StdDeserializer<Users> {

    Logger log = LoggerFactory.getLogger(this.getClass());
    public UsersDeserializer() {
        this(null);
    }

    public UsersDeserializer(Class<Users> t) {
        super(t);
    }

    @Override
    public Users deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        log.debug("UsersDeserializer.deserialize ::::");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        JsonNode node = parser.getCodec().readTree(parser);
        Date birthday = null;
        Users user = new Users();
        String userId = node.get("id").asText();
        String password = node.get("password").asText();
        String username = node.get("name").asText();
        String phone = node.get("phone").asText();
        String email = node.get("email").asText();
        String birthdayStr = node.get("birthday").asText(null);
        try {
            if(birthdayStr != null) {
                birthday = new Date(format.parse(birthdayStr).getTime());
                user.setBirthday(birthday);
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        int gender = node.get("gender").asInt();
        String address = node.get("address").asText(null);
        String comment = node.get("comment").asText(null);

        user.setAddress(address);
        user.setPassword(password);
        user.setComment(comment);
        user.setEmail(email);
        user.setGender(gender);
        user.setUserId(userId);
        user.setName(username);
        user.setPhone(phone);
        return user;
    }
}
