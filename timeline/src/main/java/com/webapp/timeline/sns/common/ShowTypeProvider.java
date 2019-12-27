package com.webapp.timeline.sns.common;

import lombok.Getter;

public enum ShowTypeProvider {

    PUBLIC_TYPE(1, "public"),
    FOLLOWER_TYPE(2, "followers"),
    PRIVATE_TYPE(3, "private");

    @Getter
    private int code;

    @Getter
    private String name;

    ShowTypeProvider(int code, String name) {
        this.code = code;
        this.name = name;
    }

}
