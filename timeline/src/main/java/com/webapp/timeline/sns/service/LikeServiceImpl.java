package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.UnauthorizedUserException;
import com.webapp.timeline.sns.domain.Likes;
import com.webapp.timeline.sns.repository.LikesRepository;
import com.webapp.timeline.sns.service.interfaces.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
public class LikeServiceImpl implements LikeService {

    private static final Logger logger = LoggerFactory.getLogger(LikeServiceImpl.class);
    private LikesRepository likesRepository;
    private ServiceAspectFactory<String> factory;

    LikeServiceImpl() {
    }

    @Autowired
    public LikeServiceImpl(LikesRepository likesRepository,
                           ServiceAspectFactory<String> factory) {
        this.likesRepository = likesRepository;
        this.factory = factory;
    }

    @Transactional
    @Override
    public void clickHeart(int postId, HttpServletRequest request) {
        logger.info("[LikeService] click heart -- start to likes...");

        factory.checkDeleteAndGetIfExist(postId);
        Likes like = Likes.builder()
                        .postId(postId)
                        .owner(factory.extractLoggedIn(request))
                        .build();

        if(likesRepository.isUserLikedPost(like) != null) {
            throw new BadRequestException();
        }

        likesRepository.save(like);
    }

    @Transactional
    @Override
    public void cancelHeart(int postId, HttpServletRequest request) {
        logger.info("[LikeService] cancel heart.");

        Likes like = Likes.builder()
                        .postId(postId)
                        .owner(factory.extractLoggedIn(request))
                        .build();
        Long likeId = likesRepository.isUserLikedPost(like);

        if(likeId == null) {
            throw new BadRequestException();
        }

        likesRepository.deleteById(likeId);
    }

}
