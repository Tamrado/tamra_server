package com.webapp.timeline.sns.service;


import com.amazonaws.services.xray.model.Http;
import com.webapp.timeline.membership.service.UserService;
import com.webapp.timeline.membership.service.UserSignService;
import com.webapp.timeline.sns.domain.Posts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

@Service("postServiceImpl")
@Transactional
public class PostServiceImpl implements PostService {

    private static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    private JpaRepository<Posts, Integer> postsRepository;
    private S3Uploader s3Uploader;
    private LinkedHashMap<Integer, String> getUrlMap;
    private UserSignService userSignService;


    @Autowired
    public void setPostsRepository(JpaRepository<Posts, Integer> postsRepository) {
        this.postsRepository = postsRepository;
    }

    @Autowired
    public void setFileUploader(S3Uploader s3Uploader) {
        this.s3Uploader = s3Uploader;
    }

    @Autowired
    public void setUserService(UserSignService userSignService) {
        this.userSignService = userSignService;
    }

    private LinkedHashMap<Integer, String> manageFileUpload(String dirName, MultipartFile[] multipartFiles) {

        if(multipartFiles.length == 0) {
            return null;
        }
        else if(multipartFiles.length > 10) {
            multipartFiles = Arrays.stream(multipartFiles, 0, 10)
                                    .toArray(MultipartFile[]::new);
        }

        s3Uploader.upload(multipartFiles, dirName);
        getUrlMap = s3Uploader.getImageUrlMap();

        System.out.println("반환하는 유알엘 : " + getUrlMap);
        return getUrlMap;
    }

    @Override
    public LinkedHashMap<Integer, String> uploadImages(HttpServletRequest httpServletRequest, MultipartFile[] multipartFiles) {
        logger.info("[PostService] Upload new " + multipartFiles.length + " files to AWS S3 / timeline.");

        String dirName = "";
        dirName = this.userSignService.extractUserFromToken(httpServletRequest).getEmail();

        return manageFileUpload(dirName, multipartFiles);
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
