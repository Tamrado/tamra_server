package com.webapp.timeline;

import com.webapp.timeline.domain.MasterId;
import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InsertUsersTest {

    @Autowired
    private UsersEntityRepository usersEntityRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void insertUsers(){
        Users users = new Users(new MasterId(8,"gsgas"),"gdgag","dgsdag","010-6345-4564","dga@naver.com",
                java.sql.Date.valueOf("2019-01-06"),0,"dsgsdag","dga","dgdsg",java.sql.Date.valueOf("2019-01-06"),1,2,3,4);
        usersEntityRepository.save(users);
        usersEntityRepository.flush();
    }

}
