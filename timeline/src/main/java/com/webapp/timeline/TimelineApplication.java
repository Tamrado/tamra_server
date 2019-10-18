package com.webapp.timeline;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"com.webapp.timeline.domain"})
@EnableJpaRepositories(basePackages = {"com.webapp.timeline.repository"})
@SpringBootApplication
public class TimelineApplication {

    public static final String APPLICATION_LOCATIONS = "spring.config.location="+
            "classpath:/application.yml,"+ "classpath:/aws.yml," + "classpath:/jwt.yml";

    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder(TimelineApplication.class)
                    .properties(APPLICATION_LOCATIONS)
                    .run(args);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
