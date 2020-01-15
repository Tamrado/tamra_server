package com.webapp.timeline.membership.service.interfaces;

import com.webapp.timeline.membership.service.response.KakaoRefreshInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface KakaoService {

    void refreshExpiredKakaoToken(String uid, HttpServletResponse response) throws RuntimeException;

    KakaoRefreshInfo requestRefreshRestAPI(HttpEntity<MultiValueMap<String, String>> httpEntity, String url) throws RuntimeException;

    void dealResponseCodeInTimeRestAPI(ResponseEntity<String> response, HttpServletResponse httpServletResponse, HttpServletRequest request);
}