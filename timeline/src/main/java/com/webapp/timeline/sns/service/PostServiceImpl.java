package com.webapp.timeline.sns.service;


import com.webapp.timeline.membership.service.UserSignService;
import com.webapp.timeline.sns.domain.Posts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service("postServiceImpl")
@Transactional
public class PostServiceImpl implements PostService {

    private static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    private JpaRepository<Posts, Integer> postsRepository;
    private PostImageS3Component postImageS3Component;
    private LinkedHashMap<Integer, String> getUrlMap;
    private UserSignService userSignService;


    @Autowired
    public void setPostsRepository(JpaRepository<Posts, Integer> postsRepository) {
        this.postsRepository = postsRepository;
    }

    @Autowired
    public void setFileUploader(PostImageS3Component postImageS3Component) {
        this.postImageS3Component = postImageS3Component;
    }

    @Autowired
    public void setUserSignService(UserSignService userSignService) {
        this.userSignService = userSignService;
    }

    @Override
    public String uploadImages(HttpServletRequest request, HttpServletResponse response,MultipartFile multipartFile) {
        logger.info("[PostService] Upload new file to AWS S3 / timeline.");

        String dirName = "";
        dirName = this.userSignService.extractUserFromToken(request,response).getEmail();

        try {
            return this.postImageS3Component.upload(multipartFile, dirName);
        }
        catch (IOException one_more_try) {

            try {
                return this.postImageS3Component.upload(multipartFile, dirName);
            }
            catch(IOException exception) {
                return null;
            }
        }
    }

    @Override
    public Posts createPost(Posts post) {

        return postsRepository.save(post);
    }

    @Override
    public Posts updatePost(Posts post) {
        return postsRepository.save(post);
    }

    @Override
    public void deletePost(Posts post) {
        postsRepository.delete(post);
    }
/*
    @Override
    public List<Posts> listAllPosts(int postId) {

    }

    @Override
    public Posts getPostById(Posts posts) {

    }


     */

}
