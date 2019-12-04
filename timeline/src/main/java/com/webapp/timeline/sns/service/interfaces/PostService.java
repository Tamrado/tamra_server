package com.webapp.timeline.sns.service.interfaces;

import com.webapp.timeline.sns.domain.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;


public interface PostService {
    Posts createPost(Posts post, HttpServletRequest request);

    String uploadImages(MultipartFile multipartFile, HttpServletRequest request);

    Posts deletePost(int postId, HttpServletRequest request);

    Posts updatePost(int postId, Posts post, HttpServletRequest request);

    Posts getOnePostByPostId(int postId, HttpServletRequest request);

    Page<Posts> getPostListByUser(String userId, Pageable pageable, HttpServletRequest request);
}
