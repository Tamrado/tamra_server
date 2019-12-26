package com.webapp.timeline.event.domain;

import com.webapp.timeline.event.action.ActionType;
import com.webapp.timeline.membership.domain.Users;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "eventType")
public abstract class AbstractAction implements Action, Comparable<AbstractAction>, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Getter
    @ManyToOne
    protected Users sender;

    @Setter
    @Getter
    @ManyToOne
    protected Users receiver;

    @Getter
    @Column(updatable = false)
    protected Date timestamp;

    @Getter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected ActionType type;

    @Column(insertable = false, updatable = false, nullable = false)
    private String eventType;

    AbstractAction() {
        timestamp = new Date();
    }

    public abstract String getLink();

    @Override
    public int compareTo(AbstractAction event) {
        return timestamp.compareTo(event.timestamp);
    }

    public Object clone() {
        Object returnObj;
        try {
            returnObj = super.clone();
            return returnObj;
        }
        catch(CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    public void initializeId() {
        this.id = null;
    }

    public String getCode() {
        return type.getCode();
    }

    public String getTopic(PasswordEncoder encoder) {
        return "/topic/user/" + encoder.encode(receiver.getEmail());
    }
}
