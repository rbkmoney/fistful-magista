package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.domain.enums.WalletEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
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
            WalletData walletData = walletDao.get(event.getSource());
            if (walletData == null) {
                throw new NotFoundException(String.format("Wallet with walletId='%s' not found", event.getSource()));
            }

            IdentityData identityData = identityDao.get(account.getIdentity());
            if (identityData == null) {
                throw new NotFoundException(String.format("Identity with identityId='%s' not found", account.getIdentity()));
            }

            walletData.setEventId(event.getId());
            walletData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            walletData.setEventOccurredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            walletData.setEventType(WalletEventType.WALLET_ACCOUNT_CREATED);
            walletData.setSequenceId(event.getPayload().getSequence());
            walletData.setWalletId(event.getSource());
            walletData.setIdentityId(account.getIdentity());
            walletData.setCurrencyCode(account.getCurrency().getSymbolicCode());

            walletData.setPartyId(identityData.getPartyId());
            walletData.setIdentityId(identityData.getIdentityId());

            walletDao.save(walletData);
            log.info("WalletAccountCreated has been saved, eventId={}, walletId={}", event.getId(), event.getSource());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
