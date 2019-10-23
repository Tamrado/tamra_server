package com.webapp.timeline.membership.web.exception;

import com.webapp.timeline.membership.service.Exception.CAuthenticationEntryPointException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/exception")
public class ExceptionController {
    @GetMapping(value = "/entrypoint")
    public void entrypointException() {
        throw new CAuthenticationEntryPointException();
    }
}
