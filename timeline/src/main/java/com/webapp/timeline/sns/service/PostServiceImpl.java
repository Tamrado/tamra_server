package com.webapp.timeline.sns.service;


import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.exception.UnauthorizedUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
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
    private UserSignServiceImpl userSignServiceImpl;


    @Autowired
    public void setPostsRepository(JpaRepository<Posts, Integer> postsRepository) {
        this.postsRepository = postsRepository;
    }

    @Autowired
    public void setFileUploader(PostImageS3Component postImageS3Component) {
        this.postImageS3Component = postImageS3Component;
    }

    @Autowired
    public void setUserSignService(UserSignServiceImpl userSignServiceImpl) {
        this.userSignServiceImpl = userSignServiceImpl;
    }

    @Override
    public String uploadImages(MultipartFile multipartFile,
                               HttpServletRequest request) {
        logger.info("[PostService] Upload new file to AWS S3 / timeline.");

        String dirName = "";
        dirName = this.userSignServiceImpl.extractUserFromToken(request).getEmail();

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
        //update content, showlevel
        return null;
    }

    @Override
    public Posts deletePost(long postId, String userId) {
        Posts post = postsRepository.findById((int)postId)
                                    .orElseThrow(EntityNotFoundException::new);

        if(userId.equals(post.getUserId())) {
            postsRepository.deleteById((int)postId);
            // 사진 지우기 through postsImagesRepository
            return post;
        }
        else {
            throw new UnauthorizedUserException();
        }
    }


}
