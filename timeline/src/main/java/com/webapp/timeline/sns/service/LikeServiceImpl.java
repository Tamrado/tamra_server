package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.BadRequestException;
import com.webapp.timeline.sns.domain.Likes;
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
import java.util.Map;

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
            profileList.add(makeSingleResponse(userId));
        });

        return LikeResponse.builder()
                            .postId(postId)
                            .profileSet(profileList)
                            .totalNum(pagingUserList.getTotalElements())
                            .first(pagingUserList.isFirst())
                            .last(pagingUserList.isLast())
                            .build();
    }

    private ProfileResponse makeSingleResponse(String userId) {
        Map<String, String> userInfo = factory.getUserProfile(userId);
        String name = userInfo.keySet()
                            .iterator()
                            .next();

        return ProfileResponse.builder()
                            .name(name)
                            .profile(userInfo.get(name))
                            .build();
    }
}
