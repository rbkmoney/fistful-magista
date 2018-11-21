package com.rbkmoney.fistful.magista.poller.handler;

import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.SinkEvent;

public interface WalletEventHandler extends EventHandler<Change, SinkEvent> {
}
