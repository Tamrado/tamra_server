package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.sns.domain.Likes;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.dto.response.LikeResponse;
import com.webapp.timeline.sns.dto.response.ProfileResponse;
import com.webapp.timeline.sns.repository.LikesRepository;
import com.webapp.timeline.sns.service.interfaces.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

import static com.webapp.timeline.sns.common.CommonTypeProvider.NEWSFEED_LIKE;
import static com.webapp.timeline.sns.common.CommonTypeProvider.NOT_COMMENT;

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

        String loggedIn = factory.extractLoggedIn(request);

        Posts post = factory.checkDeleteAndGetIfExist(postId);
        Likes like = Likes.builder()
                          .postId(postId)
                          .owner(loggedIn)
                          .build();

        if(likesRepository.isUserLikedPost(like) != null) {
            throw new BadRequestException();
        }
        factory.deliverToNewsfeed(NEWSFEED_LIKE, post, loggedIn, NOT_COMMENT);

        likesRepository.save(like);
    }

    @Transactional
    @Override
    public void cancelHeart(int postId, HttpServletRequest request) {
        logger.info("[LikeService] cancel heart.");
        String loggedIn = factory.extractLoggedIn(request);

        Likes like = Likes.builder()
                          .postId(postId)
                          .owner(loggedIn)
                          .build();
        Long likeId = likesRepository.isUserLikedPost(like);

        if(likeId == null) {
            throw new BadRequestException();
        }

        factory.withdrawFeedByLike(postId, loggedIn);
        likesRepository.deleteById(likeId);
    }

    @Override
    public LikeResponse showLikes(Pageable pageable, int postId) {
        logger.info("[LikeService] show like-list by post-Id.");
        List<ProfileResponse> profileList = new LinkedList<>();

        factory.checkDeleteAndGetIfExist(postId);
        Page<String> pagingUserList = likesRepository.showLikesByPostId(pageable, postId);

        if(factory.isPageExceed(pagingUserList, pageable)) {
            throw new BadRequestException();
        }

        pagingUserList.forEach(userId -> {
            profileList.add(factory.makeSingleProfile(userId));
        });

        return LikeResponse.builder()
                            .postId(postId)
                            .profileSet(profileList)
                            .totalNum(pagingUserList.getTotalElements())
                            .first(pagingUserList.isFirst())
                            .last(pagingUserList.isLast())
                            .build();
    }
}
