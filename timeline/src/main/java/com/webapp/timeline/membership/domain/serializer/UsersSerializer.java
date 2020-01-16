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
        jgen.writeStringField("id",users.getUserId());
        if(users.getEmail() == null)
            jgen.writeNullField("email");
        else
            jgen.writeStringField("email",users.getEmail());
        if(users.getBirthday() != null)
            jgen.writeStringField("birthday",users.getBirthday().toString());
        else
            jgen.writeNullField("birthday");
        if(users.getPhone() == null)
            jgen.writeNullField("phone");
        else
            jgen.writeStringField("phone",users.getPhone());
        jgen.writeStringField("name",users.getUsername());
        if(users.getGender() == null)
            jgen.writeNullField("gender");
        else
            jgen.writeNumberField("gender",users.getGender());
        if(users.getComment() == null)
            jgen.writeNullField("comment");
        else
            jgen.writeStringField("comment",users.getComment());
        if(users.getAddress() == null)
            jgen.writeNullField("address");
        else
            jgen.writeStringField("address",users.getAddress());
        jgen.writeStringField("timestamp",users.getTimestamp().toString());
        jgen.writeStringField("authority",users.getAuthority());
        jgen.writeEndObject();

    }
}
