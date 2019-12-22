package com.webapp.timeline.sns.web.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.webapp.timeline.sns.domain.Comments;

import java.io.IOException;

public class CustomCommentDeserializer extends StdDeserializer<Comments> {

    public CustomCommentDeserializer() {
        this(null);
    }

    public CustomCommentDeserializer(Class<Comments> t) {
        super(t);
    }

    @Override
    public Comments deserialize(JsonParser parser, DeserializationContext context)
                                                throws JsonProcessingException, IOException {

        JsonNode node = parser.getCodec().readTree(parser);
        String content = node.get("content").asText(null);

        return Comments.builder()
                        .content(content)
                        .build();
    }

}
