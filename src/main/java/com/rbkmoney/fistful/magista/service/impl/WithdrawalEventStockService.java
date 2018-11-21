package com.rbkmoney.fistful.magista.service.impl;

import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WithdrawalEventHandler;
import com.rbkmoney.fistful.magista.service.EventStockService;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WithdrawalEventStockService implements EventStockService<SinkEvent> {

    private final List<WithdrawalEventHandler> withdrawalEventHandlers;

    @Autowired
    public WithdrawalEventStockService(List<WithdrawalEventHandler> withdrawalEventHandlers) {
        this.withdrawalEventHandlers = withdrawalEventHandlers;
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
