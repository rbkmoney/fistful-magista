package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.identity.ChallengeCompleted;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeEventType;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeResolution;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
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

    private final IdentityDao identityDao;

    @Autowired
    public IdentityChallengeStatusChangedEventHandler(IdentityDao identityDao) {
        this.identityDao = identityDao;
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
            ChallengeData challengeData = identityDao.get(event.getSource(), change.getIdentityChallenge().getId());
            if (challengeData == null) {
                throw new NotFoundException(String.format("ChallengeData with identityId='%s', challengeId='%s' not found", event.getSource(), change.getIdentityChallenge().getId()));
            }

            challengeData.setEventId(event.getId());
            challengeData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            challengeData.setEventOccurredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            challengeData.setEventType(ChallengeEventType.CHALLENGE_STATUS_CHANGED);
            challengeData.setSequenceId(event.getPayload().getSequence());
            com.rbkmoney.fistful.identity.ChallengeStatus challengeStatus = change.getIdentityChallenge().getPayload().getStatusChanged();
            challengeData.setChallengeStatus(TBaseUtil.unionFieldToEnum(challengeStatus, ChallengeStatus.class));
            if (challengeStatus.isSetCompleted()) {
                ChallengeCompleted challengeCompleted = challengeStatus.getCompleted();
                challengeData.setChallengeResolution(TypeUtil.toEnumField(challengeCompleted.getResolution().toString(), ChallengeResolution.class));
                if (challengeCompleted.isSetValidUntil()) {
                    challengeData.setChallengeValidUntil(TypeUtil.stringToLocalDateTime(challengeCompleted.getValidUntil()));
                }
            }

            identityDao.save(challengeData);
            log.info("IdentityChallengeStatusChanged has been saved, eventId={}, identityId={}", event.getId(), event.getSource());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
