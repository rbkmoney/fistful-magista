package com.rbkmoney.fistful.magista.poller.handler.identity;

import com.rbkmoney.fistful.identity.Identity;
import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.IdentityEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityCreatedEventHandler implements IdentityEventHandler {

    private final IdentityDao identityDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        log.info("Trying to handle IdentityCreated: eventId={}, identityId={}", event.getEventId(), event.getSourceId());
        Identity identity = change.getChange().getCreated();

        IdentityData identityData = new IdentityData();
        identityData.setIdentityId(event.getSourceId());
        identityData.setPartyId(UUID.fromString(identity.getParty()));
        identityData.setPartyContractId(identity.getContract());
        identityData.setIdentityProviderId(identity.getProvider());
        identityData.setIdentityClassId(identity.getCls());

        identityData.setEventId(event.getEventId());
        identityData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        LocalDateTime occurredAt = TypeUtil.stringToLocalDateTime(change.getOccuredAt());
        identityData.setCreatedAt(occurredAt);
        identityData.setEventOccurredAt(occurredAt);
        identityData.setEventType(IdentityEventType.IDENTITY_CREATED);
        identityData.setIdentityId(event.getSourceId());

        try {
            identityDao.save(identityData);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }

        log.info("IdentityCreated has been saved: eventId={}, identityId={}", event.getEventId(), event.getSourceId());
    }

}
