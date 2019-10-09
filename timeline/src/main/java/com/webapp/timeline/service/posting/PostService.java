package com.webapp.timeline.service.posting;

import com.webapp.timeline.domain.PhotoVO;
import com.webapp.timeline.domain.Posts;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;


public interface PostService {
    Posts createPost(Posts post);

    List<PhotoVO> uploadImages(long postId, MultipartFile[] multipartFiles);

    Posts updatePost(Posts post);

    void deletePost(Posts post);

    /*
    public List<Posts> listAllPosts(int postId);
    public Posts getPostById(Posts posts);
    */
}
