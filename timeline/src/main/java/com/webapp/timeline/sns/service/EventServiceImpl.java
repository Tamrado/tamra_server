package com.webapp.timeline.sns.service;

import com.webapp.timeline.sns.domain.Tags;
import com.webapp.timeline.sns.dto.response.EventResponse;
import com.webapp.timeline.sns.dto.response.ProfileResponse;
import com.webapp.timeline.sns.repository.TagsRepository;
import com.webapp.timeline.sns.service.interfaces.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import static com.webapp.timeline.sns.common.CommonTypeProvider.*;

@Service
public class EventServiceImpl implements EventService {

    private TagsRepository tagsRepository;
    private ServiceAspectFactory factory;
    private static final int MAXIMUM_OF_OLD_ACTIVITIES = 20;

    EventServiceImpl() {
    }

    @Autowired
    public EventServiceImpl(TagsRepository tagsRepository,
                            ServiceAspectFactory factory) {
        this.tagsRepository = tagsRepository;
        this.factory = factory;
    }

    @Override
    public LinkedList<EventResponse> fetchActivities(HttpServletRequest request) {
        LinkedList<EventResponse> activities = new LinkedList<>();

        String receiver = factory.extractLoggedInAndActiveUser(request)
                                 .getUserId();
        LinkedList<Tags> newActivities = tagsRepository.fetchNotReadActivitiesByReceiver(receiver);
        LinkedList<Tags> oldActivities = tagsRepository.fetchAlreadyReadActivitiesByReceiver(receiver);

        if(oldActivities.size() > MAXIMUM_OF_OLD_ACTIVITIES) {
            oldActivities = (LinkedList<Tags>) oldActivities.subList(0, MAXIMUM_OF_OLD_ACTIVITIES);
        }

        newActivities.addAll(oldActivities);

        newActivities.forEach(activity -> {
            activities.add(makeSingleResponse(activity));
        });

        return activities;
    }

    private EventResponse makeSingleResponse(Tags activity) {
        ProfileResponse profile = factory.makeSingleProfile(activity.getSender());
        boolean isRead = false;

        if(activity.getRead() == READ_ALARM) {
            isRead = true;
        }

        return EventResponse.builder()
                            .sender(profile)
                            .message(profile.getName() + "님이 게시물에서 회원님을 언급하셨습니다.")
                            .timestamp(new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(activity.getTimestamp().getTime() + NINE_HOURS))
                            .dateString("")
                            .link("api/post/" + activity.getPostId() + "/detail")
                            .isRead(isRead)
                            .build();
    }

    @Transactional
    @Override
    public void makeEventsAllRead(HttpServletRequest request) {
        String receiver = factory.extractLoggedIn(request);
        tagsRepository.makeActivitiesAllRead(receiver);
    }

    @Override
    public Map<String, Long> countEvents(HttpServletRequest request) {
        String receiver = factory.extractLoggedIn(request);

        return Collections.singletonMap("count", tagsRepository.countNewActivities(receiver));
    }
}
