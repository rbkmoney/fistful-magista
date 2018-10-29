package com.rbkmoney.fistful.magista.poller;

import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.fistful.magista.service.EventStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventStockHandler<EType> implements EventHandler<EType> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final EventStockService eventStockService;

    public EventStockHandler(EventStockService eventStockService) {
        this.eventStockService = eventStockService;
    }

    @Override
    public EventAction handle(EType event, String subsKey) throws Exception {
        try {
            eventStockService.processSinkEvent(event);
            return EventAction.CONTINUE;
        } catch (Exception ex) {
            log.warn("Failed to handle event, retry", ex);
            return EventAction.DELAYED_RETRY;
        }
    }
}
