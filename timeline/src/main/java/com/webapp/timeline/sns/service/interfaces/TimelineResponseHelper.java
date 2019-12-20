package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.dto.PagingResponse;
import com.webapp.timeline.sns.dto.TimelineResponse;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public interface TimelineResponseHelper {

    List getPostImages(int postId);

    String printEasyTimestamp(Timestamp time);

    default PagingResponse<TimelineResponse> makeResponseObject(Page<Posts> pagedList,
                                                                UserImagesRepository profileRepository,
                                                                UserSignServiceImpl userService) {
        LinkedList<TimelineResponse> eventList = new LinkedList<>();

        pagedList.forEach(item -> {
            String userId = item.getAuthor();
            String profile = profileRepository.findImageURLById(userId)
                    .getProfileURL();
            String nickname = userService.loadUserByUsername(userId)
                    .getUsername();

            eventList.add(TimelineResponse.builder()
                    .author(nickname)
                    .profile(profile)
                    .content(item.getContent())
                    .showLevel(item.getShowLevel())
                    .timestamp(printEasyTimestamp(item.getLastUpdate()))
                    .files(getPostImages(item.getPostId()))
                    .totalComment(Math.toIntExact(item.getCommentNum()))
                    .build());
        });

        return PagingResponse.<TimelineResponse>builder()
                .objectSet(eventList)
                .first(pagedList.isFirst())
                .last(pagedList.isLast())
                .build();

    }
}
