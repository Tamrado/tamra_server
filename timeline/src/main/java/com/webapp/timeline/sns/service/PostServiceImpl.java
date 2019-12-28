package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.*;
import com.webapp.timeline.sns.domain.Images;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.domain.Tags;
import com.webapp.timeline.sns.dto.request.PostRequest;
import com.webapp.timeline.sns.dto.ImageDto;
import com.webapp.timeline.sns.dto.response.TimelineResponse;
import com.webapp.timeline.sns.repository.PostsRepository;
import com.webapp.timeline.sns.repository.TagsRepository;
import com.webapp.timeline.sns.service.interfaces.CommentService;
import com.webapp.timeline.sns.service.interfaces.ImageService;
import com.webapp.timeline.sns.service.interfaces.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.webapp.timeline.sns.common.CommonTypeProvider.*;
import static com.webapp.timeline.sns.common.ShowTypeProvider.FOLLOWER_TYPE;
import static com.webapp.timeline.sns.common.ShowTypeProvider.PRIVATE_TYPE;


@Service("postServiceImpl")
public class PostServiceImpl implements PostService {

    private static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    private PostsRepository postsRepository;
    private ImageService imageService;
    private CommentService commentService;
    private TimelineServiceImpl timelineService;
    private TagsRepository tagsRepository;
    private ServiceAspectFactory<Posts> factory;
    private static final int MAXIMUM_CONTENT_LENGTH = 1000;

    PostServiceImpl(){
    }

    @Autowired
    public PostServiceImpl(PostsRepository postsRepository,
                           ImageServiceImpl imageService,
                           CommentServiceImpl commentService,
                           TimelineServiceImpl timelineService,
                           TagsRepository tagsRepository,
                           ServiceAspectFactory<Posts> factory) {
        this.postsRepository = postsRepository;
        this.imageService = imageService;
        this.commentService = commentService;
        this.timelineService = timelineService;
        this.tagsRepository = tagsRepository;
        this.factory = factory;
    }

    @Transactional
    @Override
    public Map<String, Integer> createEvent(PostRequest postRequest, HttpServletRequest request) {
        logger.info("[PostService] create Post.");

        Map<String, Integer> responseBody = new HashMap<>(2);
        AtomicInteger count = new AtomicInteger();
        String author = factory.extractLoggedIn(request);

        checkContentValidation(postRequest);

        int postId = postsRepository.save(makeObjectForPost(postRequest, author))
                                    .getPostId();

        postRequest.getFiles().forEach(imageRequest -> {
            if(count.get() == TOTAL_IMAGE_MAX) {
                return;
            }

            Images entity = Images.builder()
                                .postId(postId)
                                .url(imageRequest.getOriginal())
                                .thumbnail(imageRequest.getThumbnail())
                                .deleted(NEW_EVENT_CHECK)
                                .build();
            imageService.saveImage(entity);

            count.incrementAndGet();
        });

        postRequest.getTags().forEach(tagRequest -> {
            Tags entity = Tags.builder()
                            .postId(postId)
                            .userId(tagRequest.getUsername())
                            .build();
            tagsRepository.save(entity);
        });

        responseBody.put("postId", postId);
        responseBody.put("imageNum", count.get());
        return responseBody;
    }

    @Transactional
    @Override
    public Map<String, Integer> deletePost(int postId, HttpServletRequest request) {
        logger.info("[PostService] delete Post.");
        Map<String, Integer> responseBody = new HashMap<>(3);

        Posts post = factory.checkDeleteAndGetIfExist(postId);

        if(factory.extractLoggedIn(request).equals(post.getAuthor())) {
            post.setDeleted(DELETED_EVENT_CHECK);
            factory.takeActionByQuery(this.postsRepository.markDeleteByPostId(post));

            responseBody.put("postId", postId);
            responseBody.put("imageNum", this.imageService.deleteImageByPostId(postId));
            responseBody.put("commentNum", this.commentService.removeCommentByPostId(postId));
            return responseBody;
        }
        else {
            throw new UnauthorizedUserException();
        }
    }

    @Override
    public Map<String, Integer> updatePost(int postId, PostRequest postRequest, HttpServletRequest request) {
        logger.info("[PostService] update Post.");

        Posts existedPost = factory.checkDeleteAndGetIfExist(postId);

        if(!factory.extractLoggedIn(request).equals(existedPost.getAuthor())) {
            throw new UnauthorizedUserException();
        }

        isUpdateExecuted(existedPost, postRequest);
        checkContentValidation(postRequest);

        existedPost.setContent(postRequest.getContent());
        existedPost.setShowLevel(postRequest.getShowLevel());
        existedPost.setLastUpdate(factory.whatIsTimestampOfNow());

        factory.takeActionByQuery(this.postsRepository.updatePostByPostId(existedPost));
        return Collections.singletonMap("postId", postId);
    }

    @Override
    public TimelineResponse getOnePostByPostId(int postId, HttpServletRequest request) {
        logger.info("[PostService] get one post by post-id.");

        Posts post = factory.checkDeleteAndGetIfExist(postId);
        String showLevel = post.getShowLevel();
        String loggedIn = factory.extractLoggedIn(request);
        String author = post.getAuthor();

        if(! author.equals(loggedIn)) {
            if(showLevel.equals(PRIVATE_TYPE.getName())) {
                throw new BadRequestException();
            }
            else if(showLevel.equals(FOLLOWER_TYPE.getName())
                    && !factory.isFollowedMe(loggedIn, author)) {
                throw new BadRequestException();
            }
        }
        return timelineService.makeSingleResponse(post);
    }

    private void checkContentValidation(PostRequest postRequest) {
        List<ImageDto> files = postRequest.getFiles();

        if(files == null || files.size() == 0) {
            factory.checkContentLength(postRequest.getContent(), MAXIMUM_CONTENT_LENGTH);
        }
        else {
            factory.checkContentLengthIfImageExists(postRequest.getContent(), MAXIMUM_CONTENT_LENGTH);
        }
    }

    private Posts makeObjectForPost(PostRequest event, String author) {

        return Posts.builder()
                    .author(author)
                    .content(event.getContent())
                    .lastUpdate(factory.whatIsTimestampOfNow())
                    .showLevel(event.getShowLevel())
                    .deleted(NEW_EVENT_CHECK)
                    .build();
    }

    private void isUpdateExecuted(Posts existed, PostRequest updated) {
        if(existed.getContent().equals(updated.getContent())
                && existed.getShowLevel().equals(updated.getShowLevel())) {

            throw new BadRequestException();
        }
    }
}
