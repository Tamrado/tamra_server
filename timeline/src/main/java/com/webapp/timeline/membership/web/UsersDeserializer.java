package com.webapp.timeline.membership.web;

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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
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
            birthday = new Date(format.parse(birthdayStr).getTime());
        } catch (Exception e) {
            throw new IOException(e);
        }
        int gender = node.get("gender").asInt();
        String address = node.get("address").asText(null);
        String comment = node.get("comment").asText(null);
        int group1 = node.get("group1").asInt(-1);
        int group2 = node.get("group2").asInt(-1);
        int group3 = node.get("group3").asInt(-1);
        int group4 = node.get("group4").asInt(-1);
        if(group1 != -1)
            user.setGroup1(group1);
        if(group2 != -1)
            user.setGroup2(group2);
        if(group3 != -1)
            user.setGroup3(group3);
        if(group4 != -1)
            user.setGroup4(group4);
        user.setAddress(address);
        user.setPassword(password);
        user.setBirthday(birthday);
        user.setComment(comment);
        user.setEmail(email);
        user.setGender(gender);
        user.setId(userId);
        user.setName(username);
        user.setPhone(phone);
        return user;
    }
}
