package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WithdrawalEventHandler;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.fistful.withdrawal.Withdrawal;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class WithdrawalCreatedEventHandler implements WithdrawalEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalDao withdrawalDao;
    private final WalletDao walletDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetCreated() && change.getCreated().isSetWithdrawal();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            Withdrawal withdrawal = change.getCreated().getWithdrawal();

            log.info("Trying to handle WithdrawalCreated, eventId={}, withdrawalId={}", event.getId(), event.getSource());

            WalletData walletData = getWalletData(withdrawal);

            WithdrawalData withdrawalData = new WithdrawalData();
            withdrawalData.setWithdrawalId(event.getSource());
            withdrawalData.setWalletId(withdrawal.getSource());
            withdrawalData.setPartyId(walletData.getPartyId());
            withdrawalData.setIdentityId(walletData.getIdentityId());
            withdrawalData.setDestinationId(withdrawal.getDestination());
            withdrawalData.setAmount(withdrawal.getBody().getAmount());
            withdrawalData.setCurrencyCode(withdrawal.getBody().getCurrency().getSymbolicCode());

            withdrawalData.setEventId(event.getId());
            withdrawalData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            LocalDateTime occurredAt = TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt());
            withdrawalData.setCreatedAt(occurredAt);
            withdrawalData.setEventOccurredAt(occurredAt);
            withdrawalData.setEventType(WithdrawalEventType.WITHDRAWAL_CREATED);
            withdrawalData.setSequenceId(event.getPayload().getSequence());
            withdrawalData.setWithdrawalId(event.getSource());
            withdrawalData.setWithdrawalStatus(WithdrawalStatus.pending);

            withdrawalDao.save(withdrawalData);
            log.info("WithdrawalCreated has been saved, eventId={}, withdrawalId={}", event.getId(), event.getSource());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

    private WalletData getWalletData(Withdrawal withdrawal) throws DaoException {
        WalletData walletData = walletDao.get(withdrawal.getSource());
        if (walletData == null) {
            throw new NotFoundException(String.format("WalletData with walletId='%s' not found", withdrawal.getSource()));
        }
        if (walletData.getPartyId() == null) {
            throw new IllegalStateException(String.format("PartyId not found for WalletData with walletId='%s'; it must be set for correct saving of WithdrawalCreated", withdrawal.getSource()));
        }
        return walletData;
    }

}
