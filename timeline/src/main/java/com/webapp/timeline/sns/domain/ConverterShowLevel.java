package com.webapp.timeline.sns.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ConverterShowLevel implements AttributeConverter<String, Integer> {

    @Override
    public Integer convertToDatabaseColumn(String showLevel) {
        if("public".equals(showLevel))
            return 1;
        else if("followers".equals(showLevel))
            return 2;
        else if("private".equals(showLevel))
            return 3;
        else
            return 0;
    }

    @Override
    public String convertToEntityAttribute(Integer code) {
        if(1 == code)
            return "public";
        else if(2 == code)
            return "followers";
        else if(3 == code)
            return "private";
        else
            return "cannot return your show level.";
    }
}


