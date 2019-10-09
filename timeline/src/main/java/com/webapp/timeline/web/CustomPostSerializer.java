package com.webapp.timeline.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.webapp.timeline.domain.Posts;

import java.io.IOException;

public class CustomPostSerializer extends StdSerializer<Posts> {

    public CustomPostSerializer() {
        this(null);
    }

    public CustomPostSerializer(Class<Posts> t) {
        super(t);
    }

    @Override
    public void serialize(Posts posts, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("content", posts.getContent());
        generator.writeStringField("showLevel", posts.getShowLevel());

        generator.writeEndObject();
    }
}
