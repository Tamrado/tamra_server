package com.webapp.timeline.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MasterDao {

    @Autowired
    private JdbcTemplate template;

    public void insert(int content, int content2) {
        template.update("Insert into masterkeys values(?, ?)", content, content2);
    }
}
