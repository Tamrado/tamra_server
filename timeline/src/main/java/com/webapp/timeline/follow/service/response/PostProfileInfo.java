package com.webapp.timeline.follow.service.response;


import com.webapp.timeline.membership.service.response.LoggedInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostProfileInfo {
    FollowInfo followInfo;
    LoggedInfo userInfo;
}
