package com.webapp.timeline.event.model;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class TagEventListener implements ApplicationListener<TagEvent> {


    @Override
    public void onApplicationEvent(TagEvent event) {
        //handleEvent : to tagged-users
    }
}
