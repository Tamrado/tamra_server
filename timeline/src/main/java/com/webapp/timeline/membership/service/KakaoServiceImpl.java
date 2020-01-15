package com.webapp.timeline.membership.service;

import com.google.gson.Gson;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.WrongCodeException;
import com.webapp.timeline.membership.domain.RefreshToken;
import com.webapp.timeline.membership.repository.RefreshTokenRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.JwtTokenProvider;
import com.webapp.timeline.membership.service.interfaces.KakaoService;

import com.webapp.timeline.membership.service.interfaces.UserKakaoSignService;
import com.webapp.timeline.membership.service.response.KakaoRefreshInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public class KakaoServiceImpl implements KakaoService {
    Logger log = LoggerFactory.getLogger(this.getClass());

   private RefreshTokenRepository refreshTokenRepository;
   private UserKakaoSignService userKakaoSignService;
   private JwtTokenProvider jwtTokenProvider;
   private TokenService tokenService;
   private final Gson gson = new Gson();
   private final RestTemplate restTemplate = new RestTemplate();
   @Value("${social.kakao.client_id}")
   private static String clientId;

    @Value("${social.kakao.grant_type}")
   private static String grantType;

    @Value("${social.kakao.client_secret}")
    private static String clientSecret;

    @Value("${social.url.refreshExpiredToken}")
    private static String refreshExpiredTokenUrl;

    @Autowired
    public KakaoServiceImpl(RefreshTokenRepository refreshTokenRepository
                            ,UserKakaoSignService userKakaoSignService,
                            JwtTokenProvider jwtTokenProvider
                            ,TokenService tokenService
                            ){
        this.refreshTokenRepository = refreshTokenRepository;
        this.userKakaoSignService = userKakaoSignService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenService = tokenService;
    }
    public KakaoServiceImpl(){}

    private MultiValueMap<String,String> makeParams(String uid) {
        log.info("KakaoServiceImpl:::: makeParams");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type",grantType);
        params.add("client_id",clientId);
        params.add("refresh_token",this.getUserRefreshToken(uid));
        params.add("client_secret",clientSecret);
        return params;
    }
    private HttpHeaders makeHeaders() {
        log.info("KakaoServiceImpl:::: makeHeaders");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    @Override
    public KakaoRefreshInfo requestRefreshRestAPI(HttpEntity<MultiValueMap<String,String>> httpEntity,String url) throws RuntimeException{
        log.info("KakaoServiceImpl:::: requestRefreshRestAPI");
        try{
            ResponseEntity<String> response = restTemplate.postForEntity(url,httpEntity,String.class);
            if (response.getStatusCode() == HttpStatus.OK)
                return gson.fromJson(response.getBody(), KakaoRefreshInfo.class);
            else
                throw new NoInformationException();
        } catch (Exception e) {
            throw new NoMatchPointException();
        }
    }
    private void isRefreshTokenThenStore(String uid,String refreshToken) throws RuntimeException{
        log.info("KakaoServiceImpl:::: isRefreshTokenThenStore");
        if(refreshToken == null) return;
        try {
            refreshTokenRepository.updateRefreshToken(uid, refreshToken);
        } catch (Exception e){
            throw new NoInformationException();
        }
    }

    @Override
    public void refreshExpiredKakaoToken(String uid,HttpServletResponse response) throws RuntimeException{
        log.info("KakaoServiceImpl:::: refreshExpiredKakaoToken");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
                this.makeParams(uid),
                this.makeHeaders());
       KakaoRefreshInfo kakaoRefreshInfo = this.requestRefreshRestAPI(request,refreshExpiredTokenUrl);
       userKakaoSignService.makeKakaoCookie(response,kakaoRefreshInfo.getAccess_token());
       this.isRefreshTokenThenStore(uid,kakaoRefreshInfo.getRefresh_token());
    }

    @Override
    public void dealResponseCodeInTimeRestAPI(ResponseEntity<String> response,HttpServletResponse httpServletResponse,HttpServletRequest request) throws RuntimeException{
        log.info("KakaoServiceImpl:::: dealResponseCodeInTimeRestAPI");
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED)
            this.refreshExpiredKakaoToken(tokenService.sendIdInCookie(
                    "kakaoAccesstoken",request),httpServletResponse);
        else if(response.getStatusCodeValue() == 1)
            throw new WrongCodeException();
        else
            throw new NoInformationException();
    }
    private String getUserRefreshToken(String uid) throws RuntimeException{
        log.info("KakaoServiceImpl:::: getUserRefreshToken");
        RefreshToken refreshToken = refreshTokenRepository.getOne(uid);
        return refreshToken.getRefreshToken();
    }

}
