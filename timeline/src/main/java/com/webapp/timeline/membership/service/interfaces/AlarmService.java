package com.webapp.timeline.membership.service.interfaces;

import javax.servlet.http.HttpServletRequest;

public interface AlarmService {
    void changeAlarm(Boolean isActive, HttpServletRequest request) throws RuntimeException;
    Boolean isTrueActiveAlarm(String userId) throws RuntimeException;
}
