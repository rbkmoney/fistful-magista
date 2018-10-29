package com.rbkmoney.fistful.magista.service.impl;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.IdentityEventHandler;
import com.rbkmoney.fistful.magista.service.EventStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IdentityEventStockService implements EventStockService<SinkEvent> {

    private final List<IdentityEventHandler> identityEventHandlers;

    @Autowired
    public IdentityEventStockService(List<IdentityEventHandler> identityEventHandlers) {
        this.identityEventHandlers = identityEventHandlers;
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
