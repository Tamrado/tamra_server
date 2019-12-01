package com.webapp.timeline.sns.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.webapp.timeline.sns.domain.Comments;

import java.io.IOException;

public class CustomCommentSerializer extends StdSerializer<Comments> {

    public CustomCommentSerializer() {
        this(null);
    }

    public CustomCommentSerializer(Class<Comments> t) {
        super(t);
    }

    @Override
    public void serialize(Comments comment, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("author", comment.getAuthor());
        generator.writeStringField("content", comment.getContent());
        generator.writeStringField("lastUpdate", comment.getLastUpdate().toString());
        generator.writeEndObject();
    }
}
