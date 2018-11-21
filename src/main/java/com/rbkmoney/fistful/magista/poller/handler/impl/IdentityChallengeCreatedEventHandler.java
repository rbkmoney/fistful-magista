package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.identity.Challenge;
import com.rbkmoney.fistful.identity.ChallengeChange;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.magista.dao.ChallengeDao;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeEventType;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.IdentityEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdentityChallengeCreatedEventHandler implements IdentityEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ChallengeDao challengeDao;

    @Autowired
    public IdentityChallengeCreatedEventHandler(ChallengeDao challengeDao) {
        this.challengeDao = challengeDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetIdentityChallenge()
                && change.getIdentityChallenge().getPayload().isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        ChallengeChange challengeChange = change.getIdentityChallenge();
        ChallengeData challengeData = new ChallengeData();
        challengeData.setIdentityId(event.getSource());
        challengeData.setChallengeId(challengeChange.getId());
        Challenge challenge = challengeChange.getPayload().getCreated();
        challengeData.setChallengeClassId(challenge.getCls());

        ChallengeEvent challengeEvent = new ChallengeEvent();
        challengeEvent.setEventId(event.getId());
        challengeEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        challengeEvent.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        challengeEvent.setEventType(ChallengeEventType.CHALLENGE_CREATED);
        challengeEvent.setSequenceId(event.getPayload().getSequence());
        challengeEvent.setIdentityId(event.getSource());
        challengeEvent.setChallengeId(challengeChange.getId());
        challengeEvent.setChallengeStatus(ChallengeStatus.pending);

        try {
            challengeDao.saveChallengeData(challengeData);
            challengeDao.saveChallengeEvent(challengeEvent);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
