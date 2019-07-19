package com.webapp.timeline.domain;

import com.google.common.base.*;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class MasterId implements Serializable {

    public static final int USERID_SIZE = 20;

    private int masterId;
    private String userId;

    public MasterId( int masterId,String userId) {
        Preconditions.checkArgument(userId.length() <= USERID_SIZE);
        this.userId = userId;
    }

    public MasterId() {}

    public int getMasterId() {
        return masterId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isEqual(Object object) {
        return (object instanceof MasterId);
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if(object instanceof MasterId) {
            MasterId masterkeyId = (MasterId) object;
            result = (masterkeyId.isEqual(this) &&
                    this.getMasterId() == masterkeyId.getMasterId() &&
                    this.getUserId().equals(masterkeyId.getUserId()));
        }

        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMasterId(), getUserId());
    }

}
