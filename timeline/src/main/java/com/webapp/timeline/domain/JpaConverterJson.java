package com.webapp.timeline.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.common.util.report.qual.ReportOverride;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Converter(autoApply = true)
public class JpaConverterJson implements AttributeConverter<List<PhotoVO>, String> {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<PhotoVO> photoVO) {
        try {
            return mapper.writeValueAsString(photoVO);
        } catch (JsonProcessingException e) {
            return null;
        }
    }


    @Override
    public List<PhotoVO> convertToEntityAttribute(String dbData) {
        try {
            PhotoVO[] tempArray = mapper.readValue(dbData, PhotoVO[].class);
            List<PhotoVO> toObject = Arrays.asList(tempArray);

            return toObject;

        } catch (IOException e) {
            return null;
        }
    }
}
