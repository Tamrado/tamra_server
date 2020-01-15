package com.webapp.timeline.membership.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KakaoTimeInfo {
    private Long id;
    private Long expiresInMillis;
    private Long appId;
}
