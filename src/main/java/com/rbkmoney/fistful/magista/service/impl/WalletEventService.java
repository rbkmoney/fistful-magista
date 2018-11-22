package com.rbkmoney.fistful.magista.service.impl;

import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WalletEventHandler;
import com.rbkmoney.fistful.magista.service.EventService;
import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.SinkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WalletEventService implements EventService<SinkEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<WalletEventHandler> walletEventHandlers;
    private final WalletDao walletDao;

    @Autowired
    public WalletEventService(List<WalletEventHandler> walletEventHandlers, WalletDao walletDao) {
        this.walletEventHandlers = walletEventHandlers;
        this.walletDao = walletDao;
    }

    public Optional<Long> getLastEventId(){
        try {
            Optional<Long> lastEventId = walletDao.getLastEventId();
            log.info("Last wallet eventId = {}", lastEventId);
            return lastEventId;
        } catch (DaoException e) {
            throw new StorageException("Failed to get last wallet event id", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processSinkEvent(SinkEvent event) throws StorageException {
        for (Change change : event.getPayload().getChanges()) {
            for (WalletEventHandler walletEventHandler : walletEventHandlers) {
                if (walletEventHandler.accept(change)) {
                    walletEventHandler.handle(change, event);
                }
            }
        }
    }

}
