package com.webapp.timeline.web;

import com.webapp.timeline.domain.Users;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UsersController {

    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public String postUserInfoBeforeProcessing(Users user){

    }
}
