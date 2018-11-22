package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.domain.enums.WalletEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WalletEventHandler;
import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.SinkEvent;
import com.rbkmoney.fistful.wallet.Wallet;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletCreatedEventHandler implements WalletEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WalletDao walletDao;

    @Autowired
    public WalletCreatedEventHandler(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Trying to handle WalletCreated, eventId={}, walletId={}", event.getId(), event.getSource());
        Wallet wallet = change.getCreated();
        WalletData walletData = new WalletData();
        walletData.setWalletId(event.getSource());
        walletData.setWalletName(wallet.getName());

        WalletEvent walletEvent = new WalletEvent();
        walletEvent.setEventId(event.getId());
        walletEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        walletEvent.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        walletEvent.setEventType(WalletEventType.WALLET_CREATED);
        walletEvent.setSequenceId(event.getPayload().getSequence());
        walletEvent.setWalletId(event.getSource());

        try {
            walletDao.saveWalletData(walletData);
            walletDao.saveWalletEvent(walletEvent);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
        log.info("WalletCreated has been saved, eventId={}, walletId={}", event.getId(), event.getSource());
    }

}
