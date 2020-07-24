package com.rbkmoney.fistful.magista.poller.handler.wallet;

import com.rbkmoney.fistful.magista.poller.handler.EventHandler;
import com.rbkmoney.fistful.wallet.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface WalletEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}
