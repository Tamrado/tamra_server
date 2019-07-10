package com.webapp.timeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


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
