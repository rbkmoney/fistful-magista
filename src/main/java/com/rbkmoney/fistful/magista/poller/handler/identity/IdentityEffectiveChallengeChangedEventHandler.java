package com.rbkmoney.fistful.magista.poller.handler.identity;

import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.IdentityEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityEffectiveChallengeChangedEventHandler implements IdentityEventHandler {

    private final IdentityDao identityDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetEffectiveChallengeChanged();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            log.info("Trying to handle IdentityEffectiveChallengeChanged: eventId={}, identityId={}", event.getEventId(), event.getSourceId());
            IdentityData identityData = identityDao.get(event.getSourceId());
            if (identityData == null) {
                throw new NotFoundException(String.format("IdentityEvent with id='%s' not found", event.getSourceId()));
            }

            identityData.setEventId(event.getEventId());
            identityData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            identityData.setEventOccurredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            identityData.setEventType(IdentityEventType.IDENTITY_EFFECTIVE_CHALLENGE_CHANGED);
            identityData.setIdentityEffectiveChallengeId(change.getChange().getEffectiveChallengeChanged());

            identityDao.save(identityData);
            log.info("IdentityEffectiveChallengeChanged has been saved: eventId={}, identityId={}", event.getEventId(), event.getSourceId());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }
}