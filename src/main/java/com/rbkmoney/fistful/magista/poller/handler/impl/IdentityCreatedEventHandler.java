package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.Identity;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.IdentityEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.IdentityEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Change change, SinkEvent event) {
        Identity identity = change.getCreated();
        IdentityData identityData = new IdentityData();
        identityData.setIdentityId(event.getSource());
        identityData.setPartyId(UUID.fromString(identity.getParty()));
        identityData.setPartyContractId(identity.getContract());
        identityData.setIdentityProviderId(identity.getProvider());
        identityData.setIdentityClassId(identity.getCls());

        IdentityEvent identityEvent = new IdentityEvent();
        identityEvent.setEventId(event.getPayload().getId());
        identityEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        identityEvent.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        identityEvent.setEventType(IdentityEventType.IDENTITY_CREATED);
        identityEvent.setSequenceId(event.getSequence());
        identityEvent.setIdentityId(event.getSource());
        try {
            identityDao.saveIdentityData(identityData);
            identityDao.saveIdentityEvent(identityEvent);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
