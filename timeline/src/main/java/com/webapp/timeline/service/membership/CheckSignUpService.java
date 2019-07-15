package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Users;

public interface CheckDetailedSignUpService {

    public Boolean checkIfExistingIdOverlap(Users user);
    public Boolean checkGroupOverlap(Users user);
}
