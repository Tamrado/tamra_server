package com.webapp.timeline.sns.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotBlank;


@Getter
public class CustomPageRequest {
    @JsonProperty
    @NotBlank
    private int page;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int size;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Sort.Direction direction;

    private static final int DEFAULT_SIZE = 10;
    private static final Sort.Direction DEFAULT_DIRECTION = Sort.Direction.ASC;

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public void setSize(int size) {
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public PageRequest of(String sortBy) {
        return PageRequest.of(page-1, DEFAULT_SIZE, DEFAULT_DIRECTION, sortBy);
    }
}
