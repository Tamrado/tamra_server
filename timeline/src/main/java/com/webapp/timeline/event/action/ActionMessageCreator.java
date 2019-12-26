package com.webapp.timeline.event.action;

import com.webapp.timeline.event.domain.AbstractAction;
import com.webapp.timeline.event.domain.Action;
import com.webapp.timeline.event.dto.ActionResponse;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActionMessageCreator {

    private MessageSourceAccessor messageSourceAccessor;

    @Resource(name = "eventMessageSourceAccessor")
    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    public List<ActionResponse> createMessages(List<? extends AbstractAction> actions) {
        return actions.stream()
                    .map(this::createMessage)
                    .collect(Collectors.toList());
    }

    public ActionResponse createMessage(Action action) {
        return ActionResponse.valueOf(messageSourceAccessor, action);
    }
}
