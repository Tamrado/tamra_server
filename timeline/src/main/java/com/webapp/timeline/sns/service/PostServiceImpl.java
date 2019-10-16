package com.webapp.timeline.service.posting;

import com.webapp.timeline.domain.PhotoVO;
import com.webapp.timeline.domain.PostImages;
import com.webapp.timeline.domain.Posts;
import com.webapp.timeline.service.membership.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;

@Service("postServiceImpl")
@Transactional
public class PostServiceImpl implements PostService {

    private static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    private PostImages postImages;
    private JpaRepository<Posts, Integer> postsRepository;
    private S3Uploader s3Uploader;
    private List<PhotoVO> getUrlMap;
    private UserService userService;


    @Autowired
    public void setPostsRepository(JpaRepository<Posts, Integer> postsRepository) {
        this.postsRepository = postsRepository;
    }

    @Autowired
    public void setFileUploader(S3Uploader s3Uploader) {
        this.s3Uploader = s3Uploader;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    private List<PhotoVO> manageFileUpload(String dirName, MultipartFile[] multipartFiles) {

        if(multipartFiles.length == 0) {
            return null;
        }
        else if(multipartFiles.length > 10) {
            multipartFiles = Arrays.stream(multipartFiles, 0, 10)
                                    .toArray(MultipartFile[]::new);
        }

        s3Uploader.upload(multipartFiles, dirName);
        getUrlMap = sortByPhotoId(s3Uploader.getImageUrlList());

        return getUrlMap;
    }

    /*
    * List<PhotoVO> for Json-Type 정렬
    *
    * @param list : photoId값 비교할 리스트
    *
     */
    private List<PhotoVO> sortByPhotoId(List<PhotoVO> list) {
        Collections.sort(list, (o1, o2) -> {
            if(o1 == null || o2 == null) {
                return 0;
            }
            return o1.getPhotoId() > o2.getPhotoId() ? 1 :
                    o1.getPhotoId() < o2.getPhotoId() ? -1 : 0;
        });
        return list;
    }

    @Override
    public List<PhotoVO> uploadImages(long postId, MultipartFile[] multipartFiles) {
        String dirName = "";
        dirName = this.userService.extractUserFromToken().getEmail();

        postImages = new PostImages(postId, manageFileUpload(dirName, multipartFiles));
        return null;
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
