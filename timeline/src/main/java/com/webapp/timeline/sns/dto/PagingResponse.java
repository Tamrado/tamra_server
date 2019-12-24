package com.webapp.timeline.sns.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagingResponse<T> {
    @JsonProperty("contentlist")
    List<T> objectSet;
    boolean first;
    boolean last;

}
