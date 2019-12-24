package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.*;
import com.webapp.timeline.sns.domain.Images;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.dto.request.EventRequest;
import com.webapp.timeline.sns.dto.ImageDto;
import com.webapp.timeline.sns.dto.response.TimelineResponse;
import com.webapp.timeline.sns.repository.PostsRepository;
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


@Service("postServiceImpl")
public class PostServiceImpl implements PostService {

    private static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    private PostsRepository postsRepository;
    private ImageService imageService;
    private CommentService commentService;
    private TimelineServiceImpl timelineService;
    private ServiceAspectFactory<Posts> factory;
    private static final int MAXIMUM_CONTENT_LENGTH = 1000;
    private static final String PRIVATE = "private";

    PostServiceImpl(){
    }

    @Autowired
    public PostServiceImpl(PostsRepository postsRepository,
                           ImageServiceImpl imageService,
                           CommentServiceImpl commentService,
                           TimelineServiceImpl timelineService,
                           ServiceAspectFactory<Posts> factory) {
        this.postsRepository = postsRepository;
        this.imageService = imageService;
        this.commentService = commentService;
        this.timelineService = timelineService;
        this.factory = factory;
    }

    @Transactional
    @Override
    public Map<String, Integer> createEvent(EventRequest eventRequest, HttpServletRequest request) {
        logger.info("[PostService] create Post.");

        Map<String, Integer> responseBody = new HashMap<>(2);
        AtomicInteger count = new AtomicInteger();
        String author = factory.extractLoggedIn(request);
        checkContentValidation(eventRequest);
        int postId = postsRepository.save(makeObjectForPost(eventRequest, author))
                                    .getPostId();

        eventRequest.getFiles().forEach(imageRequest -> {
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
    public Map<String, Integer> updatePost(int postId, EventRequest eventRequest, HttpServletRequest request) {
        logger.info("[PostService] update Post.");

        Posts existedPost = factory.checkDeleteAndGetIfExist(postId);

        if(!factory.extractLoggedIn(request).equals(existedPost.getAuthor())) {
            throw new UnauthorizedUserException();
        }

        isUpdateExecuted(existedPost, eventRequest);
        checkContentValidation(eventRequest);

        existedPost.setContent(eventRequest.getContent());
        existedPost.setShowLevel(eventRequest.getShowLevel());
        existedPost.setLastUpdate(factory.whatIsTimestampOfNow());

        factory.takeActionByQuery(this.postsRepository.updatePostByPostId(existedPost));
        return Collections.singletonMap("postId", postId);
    }

    @Override
    public TimelineResponse getOnePostByPostId(int postId, HttpServletRequest request) {
        logger.info("[PostService] get one post by post-id.");

        Posts post = factory.checkDeleteAndGetIfExist(postId);
        String author = post.getAuthor();
        String showLevel = post.getShowLevel();

        if(! author.equals(factory.extractLoggedIn(request))) {
            //Todo : following-level 글 처리

            if(showLevel.equals(PRIVATE)) {
                throw new BadRequestException();
            }
        }
        return timelineService.makeSingleResponse(post);
    }

    private void checkContentValidation(EventRequest eventRequest) {
        List<ImageDto> files = eventRequest.getFiles();

        if(files == null || files.size() == 0) {
            factory.checkContentLength(eventRequest.getContent(), MAXIMUM_CONTENT_LENGTH);
        }
        else {
            factory.checkContentLengthIfImageExists(eventRequest.getContent(), MAXIMUM_CONTENT_LENGTH);
        }
    }

    private Posts makeObjectForPost(EventRequest event, String author) {

        return Posts.builder()
                    .author(author)
                    .content(event.getContent())
                    .lastUpdate(factory.whatIsTimestampOfNow())
                    .showLevel(event.getShowLevel())
                    .deleted(NEW_EVENT_CHECK)
                    .build();
    }

    private void isUpdateExecuted(Posts existed, EventRequest updated) {
        if(existed.getContent().equals(updated.getContent())
                && existed.getShowLevel().equals(updated.getShowLevel())) {

            throw new BadRequestException();
        }
    }
}
