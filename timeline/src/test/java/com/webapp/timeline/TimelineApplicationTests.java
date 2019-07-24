package com.webapp.timeline;


import com.webapp.timeline.domain.Following;
import com.webapp.timeline.domain.FollowingId;
import com.webapp.timeline.repository.FollowingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TimelineApplicationTests {

    @Autowired
    private FollowingRepository followingRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void createFollowing() {
        Following following = new Following();
        following.setId(new FollowingId(new MasterId(2,"dgsdgad"), "myFriend123"));
        following.setIsAccepted(1);
        String now = "2019-07-12";
        following.setTimestamp(java.sql.Date.valueOf(now));
        followingRepository.save(following);
        followingRepository.flush();
    }



}
