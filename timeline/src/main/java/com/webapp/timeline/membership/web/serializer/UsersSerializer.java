package com.webapp.timeline.membership.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.webapp.timeline.membership.domain.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

public class UsersSerializer extends StdSerializer<Users> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    public UsersSerializer() {
        this(null);
    }

    public UsersSerializer(Class<Users> t) {
        super(t);
    }
    @Override
    public void serialize(Users user, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        Format format = new SimpleDateFormat("yyyy-mm-dd");
        jgen.writeStartObject();
        jgen.writeStringField("userId",user.getId());
        jgen.writeStringField("email",user.getEmail());
        jgen.writeStringField("name",user.getUsername());
        jgen.writeStringField("phone",user.getPhone());

        if(user.getComment() == null) jgen.writeNullField("comment");
        else jgen.writeStringField("comment",user.getComment());

        jgen.writeStringField("birthday",format.format(user.getBirthday()));
        jgen.writeNumberField("gender",user.getGender());

        if(user.getAddress() == null) jgen.writeNullField("address");
        else jgen.writeStringField("address",user.getAddress());

        jgen.writeEndObject();
    }
}