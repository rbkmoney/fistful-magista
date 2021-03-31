package com.rbkmoney.fistful.magista.handler.identity;

import com.rbkmoney.fistful.identity.ChallengeCompleted;
import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeEventType;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeResolution;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityChallengeStatusChangedEventHandler implements IdentityEventHandler {

    private final IdentityDao identityDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetIdentityChallenge()
                && change.getChange().getIdentityChallenge().getPayload().isSetStatusChanged();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {

            log.info("Trying to handle IdentityChallengeStatusChanged: eventId={}, identityId={}", event.getEventId(),
                    event.getSourceId());

            ChallengeData challengeData = getChallengeData(change, event);
            challengeData.setEventId(event.getEventId());
            challengeData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            challengeData.setEventOccurredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            challengeData.setEventType(ChallengeEventType.CHALLENGE_STATUS_CHANGED);
            var challengeStatus = change.getChange().getIdentityChallenge().getPayload().getStatusChanged();
            challengeData.setChallengeStatus(TBaseUtil.unionFieldToEnum(challengeStatus, ChallengeStatus.class));
            if (challengeStatus.isSetCompleted()) {
                ChallengeCompleted challengeCompleted = challengeStatus.getCompleted();
                challengeData.setChallengeResolution(
                        TypeUtil.toEnumField(challengeCompleted.getResolution().toString(), ChallengeResolution.class));
                if (challengeCompleted.isSetValidUntil()) {
                    challengeData
                            .setChallengeValidUntil(TypeUtil.stringToLocalDateTime(challengeCompleted.getValidUntil()));
                }
            }

            identityDao.save(challengeData);

            log.info("IdentityChallengeStatusChanged has been saved, eventId={}, identityId={}", event.getEventId(),
                    event.getSourceId());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

    private ChallengeData getChallengeData(TimestampedChange change, MachineEvent event) throws DaoException {
        ChallengeData challengeData =
                identityDao.get(event.getSourceId(), change.getChange().getIdentityChallenge().getId());
        if (challengeData == null) {
            throw new NotFoundException(String.format("ChallengeData with identityId='%s', challengeId='%s' not found",
                    event.getSourceId(), change.getChange().getIdentityChallenge().getId()));
        }
        return challengeData;
    }
}
