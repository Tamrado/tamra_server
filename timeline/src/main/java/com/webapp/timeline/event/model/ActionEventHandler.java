package com.webapp.timeline.event.model;

import com.webapp.timeline.event.action.ActionMessageCreator;
import com.webapp.timeline.event.domain.AbstractAction;
import com.webapp.timeline.event.repository.ActionRepository;
import com.webapp.timeline.membership.domain.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ActionEventHandler {

    private ActionRepository actionRepository;
    private ActionMessageCreator messageCreator;
    private SimpMessageSendingOperations simpMessageSendingOperations;
    private PasswordEncoder encoder;

    public ActionEventHandler() {
    }

    @Autowired
    public ActionEventHandler(ActionRepository actionRepository,
                              ActionMessageCreator messageCreator,
                              SimpMessageSendingOperations simpMessageSendingOperations,
                              PasswordEncoder encoder) {
        this.actionRepository = actionRepository;
        this.messageCreator = messageCreator;
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.encoder = encoder;
    }

    public void handleEvent(AbstractAction action, Users user) {
        action.setReceiver(user);
        saveAction(action);
        pushMessage(action);
    }

    private void saveAction(AbstractAction action) {
        AbstractAction newAction = (AbstractAction) action.clone();
        newAction.initializeId();
        this.actionRepository.save(newAction);
    }

    private void pushMessage(AbstractAction action) {
        this.simpMessageSendingOperations.convertAndSend(
                action.getTopic(encoder),
                this.messageCreator.createMessage(action)
        );
    }
}
