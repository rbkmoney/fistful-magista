package com.rbkmoney.fistful.magista.poller.handler.identity;

import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.fistful.magista.poller.handler.EventHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface IdentityEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}
