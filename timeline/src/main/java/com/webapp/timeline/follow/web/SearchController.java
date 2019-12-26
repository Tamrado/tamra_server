package com.webapp.timeline.follow.web;

import com.webapp.timeline.follow.service.interfaces.SearchService;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import com.webapp.timeline.sns.dto.request.CustomPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Api(tags = {"2. Search"})
@RequestMapping(value = "/api/list")
@CrossOrigin(origins = {"*"})
@RestController
public class SearchController {
    private SearchService searchService;
    public SearchController(){ }
    @Autowired
    public SearchController(SearchService searchService){
        this.searchService = searchService;
    }
    @ApiOperation(value = "친구 리스트 내 검색 기능", notes = "response : 200 성공 404 - uid 없음 411 - 친구가 없음 401 - 유저가 비활함")
    @GetMapping(value = "/{nickname}")
    public ArrayList<LoggedInfo> searchInFriendList(@PathVariable String nickname, HttpServletRequest httpServletRequest) throws RuntimeException{
        return searchService.searchInFriendList(nickname,httpServletRequest);
    }

    @ApiOperation(value = "헤더에 검색 기능", notes = "response : 200 성공 404 - uid 없음 401 - 유저가 비활함")
    @GetMapping(value = "/header/{nickname}")
    public ArrayList<LoggedInfo> searchInHeader(@PathVariable String nickname, CustomPageRequest request, HttpServletRequest httpServletRequest){
        return searchService.searchInHeader(nickname,request);
    }

}
