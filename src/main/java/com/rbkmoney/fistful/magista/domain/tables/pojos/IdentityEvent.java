/*
 * This file is generated by jOOQ.
 */
package com.rbkmoney.fistful.magista.domain.tables.pojos;


import com.rbkmoney.fistful.magista.domain.enums.IdentityEventType;

import javax.annotation.Generated;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@Generated(
        value = {
                "http://www.jooq.org",
                "jOOQ version:3.11.5"
        },
        comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class IdentityEvent implements Serializable {

    private static final long serialVersionUID = -552588366;

    private Long id;
    private Long eventId;
    private IdentityEventType eventType;
    private LocalDateTime eventCreatedAt;
    private LocalDateTime eventOccuredAt;
    private String identityId;
    private Integer sequenceId;
    private String identityEffectiveChalengeId;
    private String identityLevelId;

    public IdentityEvent() {
    }

    public IdentityEvent(IdentityEvent value) {
        this.id = value.id;
        this.eventId = value.eventId;
        this.eventType = value.eventType;
        this.eventCreatedAt = value.eventCreatedAt;
        this.eventOccuredAt = value.eventOccuredAt;
        this.identityId = value.identityId;
        this.sequenceId = value.sequenceId;
        this.identityEffectiveChalengeId = value.identityEffectiveChalengeId;
        this.identityLevelId = value.identityLevelId;
    }

    public IdentityEvent(
            Long id,
            Long eventId,
            IdentityEventType eventType,
            LocalDateTime eventCreatedAt,
            LocalDateTime eventOccuredAt,
            String identityId,
            Integer sequenceId,
            String identityEffectiveChalengeId,
            String identityLevelId
    ) {
        this.id = id;
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventCreatedAt = eventCreatedAt;
        this.eventOccuredAt = eventOccuredAt;
        this.identityId = identityId;
        this.sequenceId = sequenceId;
        this.identityEffectiveChalengeId = identityEffectiveChalengeId;
        this.identityLevelId = identityLevelId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return this.eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public IdentityEventType getEventType() {
        return this.eventType;
    }

    public void setEventType(IdentityEventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventCreatedAt() {
        return this.eventCreatedAt;
    }

    public void setEventCreatedAt(LocalDateTime eventCreatedAt) {
        this.eventCreatedAt = eventCreatedAt;
    }

    public LocalDateTime getEventOccuredAt() {
        return this.eventOccuredAt;
    }

    public void setEventOccuredAt(LocalDateTime eventOccuredAt) {
        this.eventOccuredAt = eventOccuredAt;
    }

    public String getIdentityId() {
        return this.identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public Integer getSequenceId() {
        return this.sequenceId;
    }

    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getIdentityEffectiveChalengeId() {
        return this.identityEffectiveChalengeId;
    }

    public void setIdentityEffectiveChalengeId(String identityEffectiveChalengeId) {
        this.identityEffectiveChalengeId = identityEffectiveChalengeId;
    }

    public String getIdentityLevelId() {
        return this.identityLevelId;
    }

    public void setIdentityLevelId(String identityLevelId) {
        this.identityLevelId = identityLevelId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final IdentityEvent other = (IdentityEvent) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (eventId == null) {
            if (other.eventId != null)
                return false;
        } else if (!eventId.equals(other.eventId))
            return false;
        if (eventType == null) {
            if (other.eventType != null)
                return false;
        } else if (!eventType.equals(other.eventType))
            return false;
        if (eventCreatedAt == null) {
            if (other.eventCreatedAt != null)
                return false;
        } else if (!eventCreatedAt.equals(other.eventCreatedAt))
            return false;
        if (eventOccuredAt == null) {
            if (other.eventOccuredAt != null)
                return false;
        } else if (!eventOccuredAt.equals(other.eventOccuredAt))
            return false;
        if (identityId == null) {
            if (other.identityId != null)
                return false;
        } else if (!identityId.equals(other.identityId))
            return false;
        if (sequenceId == null) {
            if (other.sequenceId != null)
                return false;
        } else if (!sequenceId.equals(other.sequenceId))
            return false;
        if (identityEffectiveChalengeId == null) {
            if (other.identityEffectiveChalengeId != null)
                return false;
        } else if (!identityEffectiveChalengeId.equals(other.identityEffectiveChalengeId))
            return false;
        if (identityLevelId == null) {
            if (other.identityLevelId != null)
                return false;
        } else if (!identityLevelId.equals(other.identityLevelId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.eventId == null) ? 0 : this.eventId.hashCode());
        result = prime * result + ((this.eventType == null) ? 0 : this.eventType.hashCode());
        result = prime * result + ((this.eventCreatedAt == null) ? 0 : this.eventCreatedAt.hashCode());
        result = prime * result + ((this.eventOccuredAt == null) ? 0 : this.eventOccuredAt.hashCode());
        result = prime * result + ((this.identityId == null) ? 0 : this.identityId.hashCode());
        result = prime * result + ((this.sequenceId == null) ? 0 : this.sequenceId.hashCode());
        result = prime * result + ((this.identityEffectiveChalengeId == null) ? 0 : this.identityEffectiveChalengeId.hashCode());
        result = prime * result + ((this.identityLevelId == null) ? 0 : this.identityLevelId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("IdentityEvent (");

        sb.append(id);
        sb.append(", ").append(eventId);
        sb.append(", ").append(eventType);
        sb.append(", ").append(eventCreatedAt);
        sb.append(", ").append(eventOccuredAt);
        sb.append(", ").append(identityId);
        sb.append(", ").append(sequenceId);
        sb.append(", ").append(identityEffectiveChalengeId);
        sb.append(", ").append(identityLevelId);

        sb.append(")");
        return sb.toString();
    }
}
