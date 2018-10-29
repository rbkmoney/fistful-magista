package com.rbkmoney.fistful.magista.poller.handler;

import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;

public interface WithdrawalEventHandler extends EventHandler<Change, SinkEvent> {
}
