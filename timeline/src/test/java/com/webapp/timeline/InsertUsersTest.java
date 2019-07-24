package com.webapp.timeline;

import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InsertUsersTest {

    @Autowired
    private UsersEntityRepository usersEntityRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void insertUsers(){
        MasterId masterId = new MasterId(1,"gdgssgas");
        Users users = new Users(masterId,"gdgag","dgsdag","010-6355-4564","dgdsgsdga@naver.com",
                java.sql.Date.valueOf("2019-01-06"),0,"dsgsdag","dga","dgdsg",java.sql.Date.valueOf("2019-01-06"),1,2,3,4);
        users.setAuthorities(masterId);
        usersEntityRepository.save(users);
        usersEntityRepository.flush();
    }

}
