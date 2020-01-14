package com.webapp.timeline.membership.domain.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.response.KakaoFirstInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class KakaoFirstDeserializer extends StdDeserializer<KakaoFirstInfo> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    public KakaoFirstDeserializer() {
        this(null);
    }

    public KakaoFirstDeserializer(Class<Users> t) {
        super(t);
    }

    @Override
    public KakaoFirstInfo deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        log.debug("KakaoFirstDeserializer.deserialize ::::");
        JsonNode node = parser.getCodec().readTree(parser);
        Long id = node.get("id").asLong();
        String uid = Long.toString(id);
        String nickname = node.get("nickname").asText();
        String thumbnail = node.get("thumbnail").asText();
        String accesstoken = node.get("accesstoken").asText();
        String refreshtoken = node.get("refreshtoken").asText();
        String email = node.get("email").asText(null);
        return new KakaoFirstInfo(uid, nickname, thumbnail,accesstoken,refreshtoken,email);
    }
}

