package com.rbkmoney.fistful.magista.handler.withdrawal;

import com.rbkmoney.fistful.magista.handler.EventHandler;
import com.rbkmoney.fistful.withdrawal.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface WithdrawalEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}
