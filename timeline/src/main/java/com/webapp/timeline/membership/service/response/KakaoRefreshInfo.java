package com.webapp.timeline.membership.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KakaoRefreshInfo {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
}
