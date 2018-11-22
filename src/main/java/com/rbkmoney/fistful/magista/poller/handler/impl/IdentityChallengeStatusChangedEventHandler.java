package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.identity.ChallengeCompleted;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.magista.dao.ChallengeDao;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeEventType;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeResolution;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.IdentityEventHandler;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdentityChallengeStatusChangedEventHandler implements IdentityEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ChallengeDao challengeDao;

    @Autowired
    public IdentityChallengeStatusChangedEventHandler(ChallengeDao challengeDao) {
        this.challengeDao = challengeDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetIdentityChallenge()
                && change.getIdentityChallenge().getPayload().isSetStatusChanged();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Trying to handle IdentityChallengeStatusChanged, eventId={}, identityId={}", event.getId(), event.getSource());
            ChallengeEvent challengeEvent = challengeDao.getLastChallengeEvent(event.getSource(), change.getIdentityChallenge().getId());
            if (challengeEvent == null) {
                throw new NotFoundException(String.format("ChallengeEvent with id='%s' not found", event.getSource()));
            }
            challengeEvent.setId(null);
            challengeEvent.setEventId(event.getId());
            challengeEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            challengeEvent.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            challengeEvent.setEventType(ChallengeEventType.CHALLENGE_STATUS_CHANGED);
            challengeEvent.setSequenceId(event.getPayload().getSequence());
            com.rbkmoney.fistful.identity.ChallengeStatus challengeStatus = change.getIdentityChallenge().getPayload().getStatusChanged();
            challengeEvent.setChallengeStatus(TBaseUtil.unionFieldToEnum(challengeStatus, ChallengeStatus.class));
            if (challengeStatus.isSetCompleted()) {
                ChallengeCompleted challengeCompleted = challengeStatus.getCompleted();
                challengeEvent.setChallengeResolution(TypeUtil.toEnumField(challengeCompleted.getResolution().toString(), ChallengeResolution.class));
                if (challengeCompleted.isSetValidUntil()) {
                    challengeEvent.setChallengeValidUntil(TypeUtil.stringToLocalDateTime(challengeCompleted.getValidUntil()));
                }
            }

            challengeDao.saveChallengeEvent(challengeEvent);
            log.info("IdentityChallengeStatusChanged has been saved, eventId={}, identityId={}", event.getId(), event.getSource());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
