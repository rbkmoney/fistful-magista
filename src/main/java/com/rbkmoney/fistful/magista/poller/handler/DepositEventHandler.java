package com.rbkmoney.fistful.magista.poller.handler;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;

public interface DepositEventHandler extends EventHandler<Change, SinkEvent> {

}
