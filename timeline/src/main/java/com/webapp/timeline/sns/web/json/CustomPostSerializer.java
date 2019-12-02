package com.webapp.timeline.sns.web.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.webapp.timeline.sns.domain.Posts;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CustomPostSerializer extends StdSerializer<Posts> {
    private static final int ONE_HOUR = 60;
    private static final int ONE_MINUTE = 60;

    public CustomPostSerializer() {
        this(null);
    }

    public CustomPostSerializer(Class<Posts> t) {
        super(t);
    }

    @Override
    public void serialize(Posts posts, JsonGenerator generator, SerializerProvider provider) throws IOException {
        String easyTimestamp = printEasyTimestamp(posts.getLastUpdate());
        generator.writeStartObject();

        generator.writeNumberField("postId", posts.getPostId());
        generator.writeStringField("author", posts.getAuthor());
        generator.writeStringField("content", posts.getContent());
        generator.writeStringField("showLevel", posts.getShowLevel());

        if(easyTimestamp.equals("0초 전") || easyTimestamp.equals("1초 전")) {
            generator.writeStringField("lastUpdate", "방금 전");
        }
        else {
            generator.writeStringField("lastUpdate", easyTimestamp);
        }

        generator.writeEndObject();
    }

    private String printEasyTimestamp(Timestamp time) {
        LocalDateTime responsedItem = time.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        if(responsedItem.isAfter(now.minus(1, ChronoUnit.HOURS))) {
            int timestamp;

            if(responsedItem.isAfter(now.minus(1, ChronoUnit.MINUTES))) {
                int secondDifference = now.getSecond() - responsedItem.getSecond();
                timestamp = secondDifference >= 0 ? secondDifference : secondDifference + ONE_MINUTE;

                return timestamp + "초 전";
            }

            int minuteDifference = now.getMinute() - responsedItem.getMinute();
            timestamp = minuteDifference > 0 ? minuteDifference : minuteDifference + ONE_HOUR;

            return timestamp + "분 전";
        }

        return new SimpleDateFormat("yyyy.MM.dd").format(time);
    }
}
