package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.Identity;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.IdentityEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.IdentityEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class IdentityCreatedEventHandler implements IdentityEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final IdentityDao identityDao;

    @Autowired
    public IdentityCreatedEventHandler(IdentityDao identityDao) {
        this.identityDao = identityDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Trying to handle IdentityCreated, eventId={}, identityId={}", event.getId(), event.getSource());
        Identity identity = change.getCreated();
        IdentityData identityData = new IdentityData();
        identityData.setIdentityId(event.getSource());
        identityData.setPartyId(UUID.fromString(identity.getParty()));
        identityData.setPartyContractId(identity.getContract());
        identityData.setIdentityProviderId(identity.getProvider());
        identityData.setIdentityClassId(identity.getCls());

        identityData.setEventId(event.getId());
        identityData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        LocalDateTime occurredAt = TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt());
        identityData.setCreatedAt(occurredAt);
        identityData.setEventOccurredAt(occurredAt);
        identityData.setEventType(IdentityEventType.IDENTITY_CREATED);
        identityData.setSequenceId(event.getPayload().getSequence());
        identityData.setIdentityId(event.getSource());
        try {
            identityDao.save(identityData);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
        log.info("IdentityCreated has been saved, eventId={}, identityId={}", event.getId(), event.getSource());
    }

}
