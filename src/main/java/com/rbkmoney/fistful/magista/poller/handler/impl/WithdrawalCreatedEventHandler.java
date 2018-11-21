package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WithdrawalEventHandler;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.fistful.withdrawal.Withdrawal;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalCreatedEventHandler implements WithdrawalEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalDao withdrawalDao;
    private final WalletDao walletDao;

    @Autowired
    public WithdrawalCreatedEventHandler(WithdrawalDao withdrawalDao, WalletDao walletDao) {
        this.withdrawalDao = withdrawalDao;
        this.walletDao = walletDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            Withdrawal withdrawal = change.getCreated();
            WalletData walletData = walletDao.getWalletData(withdrawal.getSource());
            if (walletData == null) {
                throw new NotFoundException(String.format("WalletData with walletId='%s' not found", event.getSource()));
            }

            WithdrawalData withdrawalData = new WithdrawalData();
            withdrawalData.setWithdrawalId(event.getSource());
            withdrawalData.setWalletId(withdrawal.getSource());
            withdrawalData.setPartyId(walletData.getPartyId());
            withdrawalData.setIdentityId(walletData.getIdentityId());
            withdrawalData.setDestinationId(withdrawal.getDestination());
            withdrawalData.setAmount(withdrawal.getBody().getAmount());
            withdrawalData.setCurrencyCode(withdrawal.getBody().getCurrency().getSymbolicCode());

            WithdrawalEvent withdrawalEvent = new WithdrawalEvent();
            withdrawalEvent.setEventId(event.getId());
            withdrawalEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            withdrawalEvent.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            withdrawalEvent.setEventType(WithdrawalEventType.WITHDRAWAL_CREATED);
            withdrawalEvent.setSequenceId(event.getPayload().getSequence());
            withdrawalEvent.setWithdrawalId(event.getSource());
            withdrawalEvent.setWithdrawalStatus(WithdrawalStatus.pending);

            withdrawalDao.saveWithdrawalData(withdrawalData);
            withdrawalDao.saveWithdrawalEvent(withdrawalEvent);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
