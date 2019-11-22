package com.webapp.timeline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//404
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoInformationException extends RuntimeException {
    public NoInformationException(){super();}
}
