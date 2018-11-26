package com.rbkmoney.fistful.magista.service.impl;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.IdentityEventHandler;
import com.rbkmoney.fistful.magista.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IdentityEventService implements EventService<SinkEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<IdentityEventHandler> identityEventHandlers;
    private final IdentityDao identityDao;

    public IdentityEventService(List<IdentityEventHandler> identityEventHandlers, IdentityDao identityDao) {
        this.identityEventHandlers = identityEventHandlers;
        this.identityDao = identityDao;
    }

    @Override
    public Optional<Long> getLastEventId(){
        try {
            Optional<Long> lastEventId = identityDao.getLastEventId();
            log.info("Last identity eventId = {}", lastEventId);
            return lastEventId;
        } catch (DaoException e) {
            throw new StorageException("Failed to get last identity event id", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processSinkEvent(SinkEvent event) throws StorageException {
        for (Change change : event.getPayload().getChanges()) {
            for (IdentityEventHandler identityEventHandler : identityEventHandlers) {
                if (identityEventHandler.accept(change)) {
                    identityEventHandler.handle(change, event);
                }
            }
        }
    }
}
