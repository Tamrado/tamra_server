package com.webapp.timeline.sns.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest {
    private int page;
    private int size;
    private Sort.Direction direction;

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public int getPage() {
        return this.page;
    }

    public void setSize(int size) {
        int DEFAULT_SIZE = 30;
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public int getSize() {
        return this.size;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }

    public Sort.Direction getDirection() {
        return this.direction;
    }

    public PageRequest of(String sortBy) {
        return PageRequest.of(page-1, size, direction, sortBy);
    }
}
