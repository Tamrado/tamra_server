package com.webapp.timeline.sns.web;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.timeline.sns.web.BindingError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;


public class BindingErrorsPackage {
    private static Logger logger = LoggerFactory.getLogger(BindingErrorsPackage.class);
    private List<BindingError> bindingErrors;

    public List<BindingError> getBindingErrors() {
        return bindingErrors;
    }

    public void setBindingErrors(List<BindingError> bindingErrors) {
        this.bindingErrors = bindingErrors;
    }

    public void addBindingErrors(BindingError error) {
        this.bindingErrors.add(error);
    }

    public void createErrorDetail(BindingResult bindingResult) {
        for(FieldError error : bindingResult.getFieldErrors()) {

            addBindingErrors(new BindingError
                                .BindingErrorBuilder()
                                .objectName(error.getObjectName())
                                .fieldName(error.getField())
                                .fieldValue(error.getRejectedValue().toString())
                                .message(error.getDefaultMessage()) //이거 내가 customizing : toast 블로그
                                .code(error.getCode())
                                .build());
        }
    }

    public void createCustomErrorDetail(BindingResult bindingResult) {
        for(FieldError error : bindingResult.getFieldErrors()) {

            addBindingErrors(new BindingError
                    .BindingErrorBuilder()
                    .objectName(error.getObjectName())
                    .fieldName(error.getField())
                    .fieldValue(error.getRejectedValue().toString())
                    .message(error.getDefaultMessage()) //이거 내가 customizing : toast 블로그
                    .code(error.getCode())
                    .build());
        }
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        String errorsToJson = "";

        try {
            errorsToJson = mapper.writeValueAsString(this.bindingErrors);
        } catch(JsonProcessingException e) {
            logger.error(e.getCause().toString());
        }

        return errorsToJson;
    }
}
