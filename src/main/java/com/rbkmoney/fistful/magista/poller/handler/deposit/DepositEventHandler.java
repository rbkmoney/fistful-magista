package com.rbkmoney.fistful.magista.poller.handler.deposit;

import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.magista.poller.handler.EventHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface DepositEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}
