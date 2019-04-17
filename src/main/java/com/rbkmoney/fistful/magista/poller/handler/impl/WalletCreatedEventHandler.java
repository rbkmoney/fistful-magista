package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.domain.enums.WalletEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
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

import java.time.LocalDateTime;

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

        walletData.setEventId(event.getId());
        walletData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        LocalDateTime occurredAt = TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt());
        walletData.setCreatedAt(occurredAt);
        walletData.setEventOccurredAt(occurredAt);
        walletData.setEventType(WalletEventType.WALLET_CREATED);
        walletData.setSequenceId(event.getPayload().getSequence());
        walletData.setWalletId(event.getSource());

        try {
            walletDao.save(walletData);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
        log.info("WalletCreated has been saved, eventId={}, walletId={}", event.getId(), event.getSource());
    }

}
