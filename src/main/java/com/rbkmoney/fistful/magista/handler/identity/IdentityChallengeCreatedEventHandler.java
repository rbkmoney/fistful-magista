package com.rbkmoney.fistful.magista.handler.identity;

import com.rbkmoney.fistful.identity.Challenge;
import com.rbkmoney.fistful.identity.ChallengeChange;
import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeEventType;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityChallengeCreatedEventHandler implements IdentityEventHandler {

    private final IdentityDao identityDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetIdentityChallenge()
                && change.getChange().getIdentityChallenge().getPayload().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            log.info("Trying to handle IdentityChallengeCreated: eventId={}, identityId={}", event.getEventId(), event.getSourceId());
            ChallengeChange challengeChange = change.getChange().getIdentityChallenge();
            Challenge challenge = challengeChange.getPayload().getCreated();
            LocalDateTime eventOccurredAt = TypeUtil.stringToLocalDateTime(change.getOccuredAt());

            ChallengeData challengeData = new ChallengeData();
            challengeData.setEventId(event.getEventId());
            challengeData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            challengeData.setEventOccurredAt(eventOccurredAt);
            challengeData.setEventType(ChallengeEventType.CHALLENGE_CREATED);
            challengeData.setCreatedAt(eventOccurredAt);
            challengeData.setIdentityId(event.getSourceId());
            challengeData.setChallengeId(challengeChange.getId());
            challengeData.setChallengeClassId(challenge.getCls());
            challengeData.setChallengeStatus(ChallengeStatus.pending);

            identityDao.save(challengeData);

            log.info("IdentityChallengeCreated has been saved: eventId={}, identityId={}", event.getEventId(), event.getSourceId());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
