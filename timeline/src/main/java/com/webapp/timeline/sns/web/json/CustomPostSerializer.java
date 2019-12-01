package com.webapp.timeline.sns.web.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.webapp.timeline.sns.domain.Posts;

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

        generator.writeNumberField("postId", posts.getPostId());
        generator.writeStringField("author", posts.getAuthor());
        generator.writeStringField("content", posts.getContent());
        generator.writeStringField("lastUpdate", posts.getLastUpdate().toString());
        generator.writeStringField("showLevel", posts.getShowLevel());

        generator.writeEndObject();
    }
}
