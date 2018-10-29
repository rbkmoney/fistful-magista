package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.IdentityEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
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
            IdentityEvent identityEvent = identityDao.getLastIdentityEvent(event.getSource());

            identityEvent.setId(null);
            identityEvent.setEventId(event.getPayload().getId());
            identityEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            identityEvent.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            identityEvent.setEventType(IdentityEventType.IDENTITY_LEVEL_CHANGED);
            identityEvent.setSequenceId(event.getSequence());
            identityEvent.setIdentityLevelId(change.getLevelChanged());

            identityDao.saveIdentityEvent(identityEvent);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
