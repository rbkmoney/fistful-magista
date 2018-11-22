package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.domain.enums.WalletEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WalletEventHandler;
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
    private final IdentityDao identityDao;

    @Autowired
    public WalletAccountCreatedEventHandler(WalletDao walletDao, IdentityDao identityDao) {
        this.walletDao = walletDao;
        this.identityDao = identityDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetAccount() && change.getAccount().isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Trying to handle WalletAccountCreated, eventId={}, walletId={}", event.getId(), event.getSource());
            Account account = change.getAccount().getCreated();
            WalletEvent walletEvent = walletDao.getLastWalletEvent(event.getSource());
            if (walletEvent == null) {
                throw new NotFoundException(String.format("WalletEvent with walletId='%s' not found", event.getSource()));
            }

            WalletData walletData = walletDao.getWalletData(walletEvent.getWalletId());
            if (walletData == null) {
                throw new NotFoundException(String.format("WalletData with walletId='%s' not found", event.getSource()));
            }

            IdentityData identityData = identityDao.getIdentityData(account.getIdentity());
            if (identityData == null) {
                throw new NotFoundException(String.format("IdentityData with identityId='%s' not found", account.getIdentity()));
            }

            walletEvent.setId(null);
            walletEvent.setEventId(event.getId());
            walletEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            walletEvent.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            walletEvent.setEventType(WalletEventType.WALLET_ACCOUNT_CREATED);
            walletEvent.setSequenceId(event.getPayload().getSequence());
            walletEvent.setWalletId(event.getSource());
            walletEvent.setIdentityId(account.getIdentity());
            walletEvent.setCurrencyCode(account.getCurrency().getSymbolicCode());

            walletData.setPartyId(identityData.getPartyId());

            walletDao.saveWalletData(walletData);
            walletDao.saveWalletEvent(walletEvent);
            log.info("WalletAccountCreated has been saved, eventId={}, walletId={}", event.getId(), event.getSource());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
