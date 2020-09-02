package com.rbkmoney.fistful.magista.handler.identity;

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
        try {
            log.info("Trying to handle IdentityCreated: eventId={}, identityId={}", event.getEventId(), event.getSourceId());

            Identity identity = change.getChange().getCreated();
            LocalDateTime occurredAt = TypeUtil.stringToLocalDateTime(change.getOccuredAt());

            IdentityData identityData = new IdentityData();
            identityData.setEventId(event.getEventId());
            identityData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            identityData.setCreatedAt(occurredAt);
            identityData.setEventOccurredAt(occurredAt);
            identityData.setEventType(IdentityEventType.IDENTITY_CREATED);
            identityData.setIdentityId(event.getSourceId());
            identityData.setPartyId(UUID.fromString(identity.getParty()));
            identityData.setPartyContractId(identity.getContract());
            identityData.setIdentityProviderId(identity.getProvider());
            identityData.setIdentityClassId(identity.getCls());

            identityDao.save(identityData);

            log.info("IdentityCreated has been saved: eventId={}, identityId={}", event.getEventId(), event.getSourceId());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
