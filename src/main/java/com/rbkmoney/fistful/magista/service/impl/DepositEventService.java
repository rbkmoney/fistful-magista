package com.rbkmoney.fistful.magista.service.impl;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.DepositEventHandler;
import com.rbkmoney.fistful.magista.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositEventService implements EventService<SinkEvent> {

    private final List<DepositEventHandler> eventHandlers;
    private final DepositDao depositDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Long> getLastEventId() {
        try {
            Optional<Long> lastEventId = depositDao.getLastEventId();
            log.info("Last deposit eventId = {}", lastEventId);
            return lastEventId;
        } catch (DaoException e) {
            throw new StorageException("Failed to get last deposit event id", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processSinkEvent(SinkEvent event) {
        for (Change change : event.getPayload().getChanges()) {
            for (DepositEventHandler identityEventHandler : eventHandlers) {
                if (identityEventHandler.accept(change)) {
                    identityEventHandler.handle(change, event);
                }
            }
        }
    }
}
