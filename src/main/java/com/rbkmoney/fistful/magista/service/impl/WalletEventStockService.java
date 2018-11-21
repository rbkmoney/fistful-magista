package com.rbkmoney.fistful.magista.service.impl;

import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WalletEventHandler;
import com.rbkmoney.fistful.magista.service.EventStockService;
import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.SinkEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletEventStockService implements EventStockService<SinkEvent> {

    private final List<WalletEventHandler> walletEventHandlers;

    @Autowired
    public WalletEventStockService(List<WalletEventHandler> walletEventHandlers) {
        this.walletEventHandlers = walletEventHandlers;
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
