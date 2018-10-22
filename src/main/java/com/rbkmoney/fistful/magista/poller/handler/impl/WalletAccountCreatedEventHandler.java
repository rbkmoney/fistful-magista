package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.domain.enums.WalletEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WalletEventHandler;
import com.rbkmoney.fistful.wallet.Account;
import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletAccountCreatedEventHandler implements WalletEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WalletDao walletDao;

    @Autowired
    public WalletAccountCreatedEventHandler(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetAccount() && change.getAccount().isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            WalletEvent walletEvent = walletDao.getLastWalletEvent(event.getSource());

            walletEvent.setId(null);
            walletEvent.setEventId(event.getPayload().getId());
            walletEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            walletEvent.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            walletEvent.setEventType(WalletEventType.WALLET_ACCOUNT_CREATED);
            walletEvent.setSequenceId(event.getSequence());
            walletEvent.setWalletId(event.getSource());
            Account account = change.getAccount().getCreated();
            walletEvent.setIdentityId(account.getIdentity());
            walletEvent.setCurrencyCode(account.getCurrency().getSymbolicCode());

            walletDao.saveWalletEvent(walletEvent);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
