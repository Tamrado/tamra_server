package com.webapp.timeline.sns.service;

import com.webapp.timeline.exception.*;
import com.webapp.timeline.follow.service.FriendServiceImpl;
import com.webapp.timeline.follow.service.interfaces.FriendService;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.service.UserSignServiceImpl;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import com.webapp.timeline.sns.domain.Newsfeed;
import com.webapp.timeline.sns.domain.Posts;
import com.webapp.timeline.sns.dto.response.ProfileResponse;
import com.webapp.timeline.sns.repository.NewsfeedRepository;
import com.webapp.timeline.sns.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.webapp.timeline.sns.common.CommonTypeProvider.*;
import static com.webapp.timeline.sns.common.ShowTypeProvider.FOLLOWER_TYPE;
import static com.webapp.timeline.sns.common.ShowTypeProvider.PUBLIC_TYPE;

@Service
public class ServiceAspectFactory<T> {
    private UserSignServiceImpl userSignService;
    private FriendService friendService;
    private NewsfeedRepository newsfeedRepository;
    private PostsRepository postsRepository;
    private UserImagesRepository userImagesRepository;

    private static final String INACTIVE_USER = "ROLE_INACTIVEUSER";

    ServiceAspectFactory() {
    }

    @Autowired
    public ServiceAspectFactory (UserSignServiceImpl userSignService,
                                 FriendServiceImpl friendService,
                                 NewsfeedRepository newsfeedRepository,
                                 PostsRepository postsRepository,
                                 UserImagesRepository userImagesRepository) {
        this.userSignService = userSignService;
        this.friendService = friendService;
        this.newsfeedRepository = newsfeedRepository;
        this.postsRepository = postsRepository;
        this.userImagesRepository = userImagesRepository;
    }

    protected String extractLoggedIn(HttpServletRequest request) {
        String loggedIn = "";
        try {
            loggedIn = this.userSignService.extractUserFromToken(request)
                                        .getUserId();
        }
        catch(NoInformationException no_user) {
            throw new UnauthorizedUserException();
        }

        return loggedIn;
    }

    protected void checkInactiveUser(String userId) {
        Users userInfo = this.userSignService.loadUserByUsername(userId);

        if(userInfo.getAuthority().equals(INACTIVE_USER)) {
            throw new NoInformationException();
        }
    }

    protected ProfileResponse makeSingleProfile(String userId) {
        LoggedInfo userInfo = getUserInfo(userId);

        return ProfileResponse.builder()
                            .name(userInfo.getNickname())
                            .profile(userInfo.getThumbnail())
                            .build();
    }

    protected LoggedInfo getUserInfo(String userId) {
        String profile = this.userImagesRepository.findImageURLById(userId)
                                                .getProfileURL();
        Users user = this.userSignService.loadUserByUsername(userId);

        return new LoggedInfo(userId, profile, user.getName(), user.getComment());
    }

    @SuppressWarnings("unchecked")
    protected void deliverToNewsfeed(String category, Posts post, String sender, long commentId) {
        List<String> receivers = new ArrayList<>();

        if(post.getAuthor().equals(sender)) {
            return;
        }

        List<String> followers = whoFollowsMe(sender);

        if(post.getShowLevel().equals(PUBLIC_TYPE.getName())) {
            receivers = followers;
        }
        else if(post.getShowLevel().equals(FOLLOWER_TYPE.getName())) {
            List<String> followersOfAuthor = whoFollowsMe(post.getAuthor());
            followersOfAuthor.retainAll(followers);

            receivers = followersOfAuthor;
        }

        receivers.forEach(follower -> {
            Newsfeed feed = Newsfeed.builder()
                                    .postId(post.getPostId())
                                    .category(category)
                                    .sender(sender)
                                    .receiver(follower)
                                    .commentId(commentId)
                                    .build();
            deliver(feed);
        });
    }

    protected void deliver(Newsfeed feed) {
        this.newsfeedRepository.save(feed);
    }

    protected void withdrawFeedByPostId(int postId) {
        this.newsfeedRepository.deleteNewsfeedByPostId(postId);
    }

    protected void withdrawFeedByComment(String sender, long commentId) {
        this.newsfeedRepository.deleteNewsfeedByComment(sender, commentId);
    }

    protected void withdrawFeedByLike(int postId, String sender) {
        this.newsfeedRepository.deleteNewsfeedByLike(postId, sender, NEWSFEED_LIKE);
    }

    protected List whoFollowsMe(String me) {
        try {
            return this.friendService.sendFollowIdList(me, false);
        }
        catch(NoMatchPointException no_one) {
            return Collections.EMPTY_LIST;
        }
    }

    protected boolean isFollowedMe(String loggedIn, String author) {
        try {
            return this.friendService.sendFollowIdList(author, false)
                                     .contains(loggedIn);
        }
        catch(NoMatchPointException no_friend) {
            return false;
        }
    }

    protected Posts checkDeleteAndGetIfExist(int postId) {
        Posts post = this.postsRepository.findById(postId)
                                        .orElseThrow(NoInformationException::new);
        if(post.getDeleted() == DELETED_EVENT_CHECK) {
            throw new NoInformationException();
        }

        return post;
    }

    protected Timestamp whatIsTimestampOfNow() {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        String now = LocalDateTime.now()
                .atZone(zoneId)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return Timestamp.valueOf(now);
    }

    protected void takeActionByQuery(int affectedRow) {
        if(affectedRow == 0) {
            throw new NoInformationException();
        }
    }

    protected void checkContentLength(String content, int maxLength) {
        if(content.length() == 0 || content.length() > maxLength) {
            throw new NoStoringException();
        }
    }

    protected boolean isPageExceed(Page<T> pagingList, Pageable pageable) {
        int current = pageable.getPageNumber();
        int lastPage = pagingList.getTotalPages() - 1;

        if(current > lastPage) {
            return true;
        }
        return false;
    }
}
