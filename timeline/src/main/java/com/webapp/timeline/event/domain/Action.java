package com.webapp.timeline.event.domain;

import java.util.Date;

public interface Action {
    Object[] getArguments();

    String getCode();

    Date getTimestamp();

    String getLink();
}
