package com.webapp.timeline.web;

import com.webapp.timeline.dao.MasterDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

    @Autowired
    private MasterDao masterDao;


    @RequestMapping("/")
    public String index(){
        masterDao.insert(1, 2);
        return "Success";

    }
}
