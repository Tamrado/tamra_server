package com.webapp.timeline.sns.web.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.webapp.timeline.sns.domain.Posts;


import java.io.IOException;

public class CustomPostDeserializer extends StdDeserializer<Posts> {

    public CustomPostDeserializer() {
        this(null);
    }

    public CustomPostDeserializer(Class<Posts> t) {
        super(t);
    }

    @Override
    public Posts deserialize(JsonParser parser, DeserializationContext context)
                                            throws JsonProcessingException, IOException {

        JsonNode node = parser.getCodec().readTree(parser);

        String content = node.get("content").asText(null);
        String showLevel = node.get("showLevel").asText("public");

        return new Posts.Builder()
                        .content(content)
                        .showLevel(showLevel)
                        .build();
    }
}
