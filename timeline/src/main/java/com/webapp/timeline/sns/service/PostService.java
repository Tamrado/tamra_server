package com.webapp.timeline.sns.service;


import com.webapp.timeline.sns.domain.Posts;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;


public interface PostService {
    Posts createPost(Posts post);

    String uploadImages(MultipartFile multipartFile, HttpServletRequest request);

    Posts updatePost(Posts post);

    Posts deletePost(long postId, String userId);

    /*
    public List<Posts> listAllPosts(int postId);
    public Posts getPostById(Posts posts);
    */
}
