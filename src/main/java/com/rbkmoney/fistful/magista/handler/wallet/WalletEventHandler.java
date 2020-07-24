package com.rbkmoney.fistful.magista.handler.wallet;

import com.rbkmoney.fistful.magista.handler.EventHandler;
import com.rbkmoney.fistful.wallet.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface WalletEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}
