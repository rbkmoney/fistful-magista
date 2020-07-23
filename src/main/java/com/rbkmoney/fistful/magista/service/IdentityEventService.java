package com.rbkmoney.fistful.magista.service;

import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.fistful.magista.poller.handler.identity.IdentityEventHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityEventService {

    private final List<IdentityEventHandler> identityEventHandlers;
    private final MachineEventParser<TimestampedChange> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(MachineEvent machineEvent) {
        TimestampedChange change = parser.parse(machineEvent);

        if (change.isSetChange()) {
            identityEventHandlers.stream()
                    .filter(handler -> handler.accept(change))
                    .forEach(handler -> handler.handle(change, machineEvent));
        }
    }
}
