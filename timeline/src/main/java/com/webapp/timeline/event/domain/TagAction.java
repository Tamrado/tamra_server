package com.webapp.timeline.event.domain;

import com.webapp.timeline.event.action.ActionType;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.sns.domain.Posts;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@DiscriminatorValue("TagAction")
public class TagAction extends AbstractAction {

    @ManyToOne
    private Posts post;

    private TagAction(Users sender, Users receiver, Posts post, ActionType type) {
        this.sender = sender;
        this.receiver = receiver;
        this.post = post;
        this.type = type;
    }

    public static TagAction valueOf(Users sender, Users receiver, Posts post, ActionType type) {
        return new TagAction(sender, receiver, post, type);
    }

    @Override
    public Object[] getArguments() {
        return new Object[] {
                sender.getUsername(),
                receiver.getUsername(),
                getPost().getContent()
        };
    }

    public Posts getPost() {
        return post;
    }

    @Override
    public String getLink() {
        return "post/" + getPost().getPostId() + "/detail";
    }
}
