package com.webapp.timeline.service.membership;

import com.webapp.timeline.domain.Users;

public interface CheckSignUpService {

    public Boolean checkIfExistingIdOverlap(Users user);
    public Boolean checkIfExistingEmailOverlap(Users user);
    public Boolean checkIfExistingPhoneOverlap(Users user);
    public Boolean checkOverlappingGroup(Users user);
}
