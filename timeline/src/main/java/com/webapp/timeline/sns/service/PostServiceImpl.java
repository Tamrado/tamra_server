package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.*;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.service.UserSignService;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.repository.PostsRepository;
import com.webapp.timeline.sns.service.interfaces.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


@Service("postServiceImpl")
public class PostServiceImpl implements PostService {

    private static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    private PostsRepository postsRepository;
    private UserSignServiceImpl userSignService;
    private ServiceAspectFactory<Posts> factory;
    private static final int MAXIMUM_CONTENT_LENGTH = 1000;
    private static final int NEW_POST_CHECK = 0;
    private static final int DELETED_POST_CHECK = 1;
    private static final String PRIVATE = "private";
    private static final String INACTIVE_USER = "ROLE_INACTIVEUSER";

    @Autowired
    public void setPostsRepository(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    @Autowired
    public void setUserSignService(UserSignServiceImpl userSignServiceImpl) {
        this.userSignService = userSignServiceImpl;
    }

    @Autowired
    public void setServiceAspectFactory(ServiceAspectFactory<Posts> factory) {
        this.factory = factory;
    }

    private Posts checkIfPostDeleted(int postId) {
        Posts post = this.postsRepository.findById(postId)
                                        .orElseThrow(NoInformationException::new);
        if(post.getDeleted() == DELETED_POST_CHECK) {
            throw new NoInformationException();
        }

        return post;
    }

    @Override
    public void createPost(Posts post, HttpServletRequest request) {
        logger.info("[PostService] create Post.");

        String author = this.userSignService.extractUserFromToken(request)
                                            .getUserId();

        factory.checkContentLength(post.getContent(), MAXIMUM_CONTENT_LENGTH);
        postsRepository.save(makeObjectForPost(post, author));
    }

    private Posts makeObjectForPost(Posts post, String author) {

        return new Posts.Builder()
                        .author(author)
                        .content(post.getContent())
                        .lastUpdate(factory.whatIsTimestampOfNow())
                        .showLevel(post.getShowLevel())
                        .deleted(NEW_POST_CHECK)
                        .build();
    }

    //Todo : 배치에서 post, postsImages, Comments 에서 다 삭제처리하기
    @Override
    public Posts deletePost(int postId, HttpServletRequest request) {
        logger.info("[PostService] delete Post.");

        Posts post = checkIfPostDeleted(postId);
        String author = this.userSignService.extractUserFromToken(request)
                                            .getUserId();

        if(author.equals(post.getAuthor())) {
            post.setDeleted(DELETED_POST_CHECK);
            int affectedRow = this.postsRepository.markDeleteByPostId(post);

            return factory.takeActionByQuery(post, affectedRow);
        }
        else {
            throw new UnauthorizedUserException();
        }
    }

    @Override
    public Posts updatePost(int postId, Posts post, HttpServletRequest request) {
        logger.info("[PostService] update Post.");

        Posts existedPost = checkIfPostDeleted(postId);
        String author = this.userSignService.extractUserFromToken(request)
                                            .getUserId();

        if(author.equals(existedPost.getAuthor())) {
            isUpdateExecuted(existedPost, post);
            factory.checkContentLength(post.getContent(), MAXIMUM_CONTENT_LENGTH);

            existedPost.setContent(post.getContent());
            existedPost.setShowLevel(post.getShowLevel());
            existedPost.setLastUpdate(factory.whatIsTimestampOfNow());

            int affectedRow = this.postsRepository.updatePostByPostId(existedPost);
            return factory.takeActionByQuery(existedPost, affectedRow);
        }
        else {
            throw new UnauthorizedUserException();
        }
    }

    private void isUpdateExecuted(Posts existed, Posts updated) {

        if(existed.getContent().equals(updated.getContent())
            && existed.getShowLevel().equals(updated.getShowLevel())) {

            throw new BadRequestException();
        }
    }

    @Override
    public Posts getOnePostByPostId(int postId, HttpServletRequest request) {
        logger.info("[PostService] get one post by post-id.");

        Posts post = checkIfPostDeleted(postId);
        String author = post.getAuthor();
        String loggedIn = this.userSignService.extractUserFromToken(request)
                                            .getUserId();
        String showLevel = post.getShowLevel();

        if(! author.equals(loggedIn)) {
            //Todo : following-level 글 처리

            if(showLevel.equals(PRIVATE)) {
                throw new BadRequestException();
            }
        }
        return post;
    }

    @Override
    public Page<Posts> getPostListByUser(String userId, Pageable pageable, HttpServletRequest request) {
        logger.info("[PostService] get post-list by user-id.");
        String loggedIn;
        checkInactiveUser(userId);
        Page<Posts> pagingPostList;

        try {
            loggedIn = this.userSignService.extractUserFromToken(request)
                                        .getUserId();

            if(loggedIn.equals(userId)) {
                pagingPostList = this.postsRepository.listMyPostsByUser(pageable, loggedIn);

                System.out.println(pagingPostList.getTotalPages() - 1);
                System.out.println(pageable.getPageNumber());
            }

            pagingPostList = this.postsRepository.listPublicPostsByUser(pageable, userId);
        }
        catch(NoMatchPointException not_logged_in) {
            pagingPostList = this.postsRepository.listPublicPostsByUser(pageable, userId);

        }

        if(factory.isPageExceed(pagingPostList, pageable)) {
            throw new BadRequestException();
        }
        //Todo : following 중인지 검사 -> following중이면 followers 허용 글까지 볼 수 있게
        return pagingPostList;
    }

    private void checkInactiveUser(String userId) {
        Users userInfo = this.userSignService.loadUserByUsername(userId);

        if(userInfo.getAuthority().equals(INACTIVE_USER)) {
            throw new NoInformationException();
        }
    }
}
