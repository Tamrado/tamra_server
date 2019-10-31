package com.webapp.timeline.sns.service;


import com.webapp.timeline.sns.domain.Posts;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;


public interface PostService {
    Posts createPost(Posts post);

    String uploadImages(HttpServletRequest request, MultipartFile multipartFile);

    Posts updatePost(Posts post);

    void deletePost(Posts post);

    /*
    public List<Posts> listAllPosts(int postId);
    public Posts getPostById(Posts posts);
    */
}
