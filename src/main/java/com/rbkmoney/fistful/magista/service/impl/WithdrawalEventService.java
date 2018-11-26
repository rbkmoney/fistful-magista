package com.rbkmoney.fistful.magista.service.impl;

import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WithdrawalEventHandler;
import com.rbkmoney.fistful.magista.service.EventService;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WithdrawalEventService implements EventService<SinkEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<WithdrawalEventHandler> withdrawalEventHandlers;
    private final WithdrawalDao withdrawalDao;

    @Autowired
    public WithdrawalEventService(List<WithdrawalEventHandler> withdrawalEventHandlers, WithdrawalDao withdrawalDao) {
        this.withdrawalEventHandlers = withdrawalEventHandlers;
        this.withdrawalDao = withdrawalDao;
    }

    public Optional<Long> getLastEventId(){
        try {
            Optional<Long> lastEventId = withdrawalDao.getLastEventId();
            log.info("Last withdrawal eventId = {}", lastEventId);
            return lastEventId;
        } catch (DaoException e) {
            throw new StorageException("Failed to get last withdrawal event id", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processSinkEvent(SinkEvent event) throws StorageException {
        for (Change change : event.getPayload().getChanges()) {
            for (WithdrawalEventHandler withdrawalEventHandler : withdrawalEventHandlers) {
                if (withdrawalEventHandler.accept(change)) {
                    withdrawalEventHandler.handle(change, event);
                }
            }
        }
    }

}
