package com.webapp.timeline.membership.domain.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.webapp.timeline.membership.domain.Users;

import java.io.IOException;

public class UsersSerializer extends StdSerializer<Users> {
    public UsersSerializer() {
        this(null);
    }

    public UsersSerializer(Class<Users> t) {
        super(t);
    }
    @Override
    public void serialize(Users users, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("userId",users.getId());
        jgen.writeStringField("email",users.getEmail());
        jgen.writeStringField("birthday",users.getBirthday().toString());
        jgen.writeStringField("phone",users.getPhone());
        jgen.writeStringField("name",users.getUsername());
        jgen.writeNumberField("gender",users.getGender());
        jgen.writeStringField("comment",users.getComment());
        jgen.writeStringField("address",users.getAddress());
        jgen.writeStringField("timestamp",users.getTimestamp().toString());
        jgen.writeStringField("authority",users.getAuthority());
        jgen.writeEndObject();

    }
}
