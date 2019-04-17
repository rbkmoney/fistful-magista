package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.identity.Challenge;
import com.rbkmoney.fistful.identity.ChallengeChange;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeEventType;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.IdentityEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IdentityChallengeCreatedEventHandler implements IdentityEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final IdentityDao identityDao;

    @Autowired
    public IdentityChallengeCreatedEventHandler(IdentityDao identityDao) {
        this.identityDao = identityDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetIdentityChallenge()
                && change.getIdentityChallenge().getPayload().isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Trying to handle IdentityChallengeCreated, eventId={}, identityId={}", event.getId(), event.getSource());
        ChallengeChange challengeChange = change.getIdentityChallenge();
        ChallengeData challengeData = new ChallengeData();
        challengeData.setIdentityId(event.getSource());
        challengeData.setChallengeId(challengeChange.getId());
        Challenge challenge = challengeChange.getPayload().getCreated();
        challengeData.setChallengeClassId(challenge.getCls());

        challengeData.setEventId(event.getId());
        challengeData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        LocalDateTime eventOccurredAt = TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt());
        challengeData.setCreatedAt(eventOccurredAt);
        challengeData.setEventOccurredAt(eventOccurredAt);
        challengeData.setEventType(ChallengeEventType.CHALLENGE_CREATED);
        challengeData.setSequenceId(event.getPayload().getSequence());
        challengeData.setIdentityId(event.getSource());
        challengeData.setChallengeId(challengeChange.getId());
        challengeData.setChallengeStatus(ChallengeStatus.pending);

        try {
            identityDao.save(challengeData);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
        log.info("IdentityChallengeCreated has been saved, eventId={}, identityId={}", event.getId(), event.getSource());
    }

}
