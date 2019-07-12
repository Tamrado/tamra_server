package com.webapp.timeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EntityScan(basePackages = {"com.webapp.timeline.domain"})
@EnableJpaRepositories(basePackages = {"com.webapp.timeline.repository"})
@SpringBootApplication
public class TimelineApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(TimelineApplication.class, args);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
