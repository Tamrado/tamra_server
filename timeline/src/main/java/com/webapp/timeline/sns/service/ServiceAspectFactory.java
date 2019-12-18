package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.exception.WrongCodeException;
import com.webapp.timeline.sns.domain.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ServiceAspectFactory<T> {

    protected Timestamp whatIsTimestampOfNow() {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        String now = LocalDateTime.now()
                .atZone(zoneId)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return Timestamp.valueOf(now);
    }

    protected T takeActionByQuery(T object, int affectedRow) {
        if(affectedRow == 0) {
            throw new WrongCodeException();
        }

        return object;
    }

    protected void checkContentLength(String content, int maxLength) {
        if(content.length() == 0 || content.length() > maxLength) {
            throw new NoStoringException();
        }
    }

    protected boolean isPageExceed(Page<T> pagingList, Pageable pageable) {
        int current = pageable.getPageNumber();
        int lastPage = pagingList.getTotalPages() - 1;

        if(current > lastPage) {
            return true;
        }
        return false;
    }

}
