package com.webapp.timeline.event.model;

import com.webapp.timeline.event.domain.AbstractAction;
import org.springframework.context.ApplicationEvent;

public class TagEvent extends ApplicationEvent {

    private AbstractAction action;

    public TagEvent(Object object, AbstractAction action) {
        super(object);
        this.action = action;
    }

    public AbstractAction getAction() {
        return this.action;
    }
}
