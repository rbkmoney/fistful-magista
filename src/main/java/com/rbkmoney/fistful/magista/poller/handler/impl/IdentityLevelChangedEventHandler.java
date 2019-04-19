package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.IdentityEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.IdentityEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdentityLevelChangedEventHandler implements IdentityEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final IdentityDao identityDao;

    @Autowired
    public IdentityLevelChangedEventHandler(IdentityDao identityDao) {
        this.identityDao = identityDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetLevelChanged();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Trying to handle IdentityLevelChanged, eventId={}, identityId={}", event.getId(), event.getSource());
            IdentityData identityData = identityDao.get(event.getSource());
            if (identityData == null) {
                throw new NotFoundException(String.format("Identity with id='%s' not found", event.getSource()));
            }
            identityData.setEventId(event.getId());
            identityData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            identityData.setEventOccurredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            identityData.setEventType(IdentityEventType.IDENTITY_LEVEL_CHANGED);
            identityData.setSequenceId(event.getPayload().getSequence());
            identityData.setIdentityLevelId(change.getLevelChanged());

            identityDao.save(identityData);
            log.info("IdentityLevelChanged has been saved, eventId={}, identityId={}", event.getId(), event.getSource());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
