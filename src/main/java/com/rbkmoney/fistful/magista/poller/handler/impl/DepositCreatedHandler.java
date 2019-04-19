package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.Deposit;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.DepositEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class DepositCreatedHandler implements DepositEventHandler {

    private final DepositDao depositDao;
    private final WalletDao walletDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start deposit created handling, eventId={}, depositId={}", event.getId(), event.getSource());
            Deposit deposit = change.getCreated();

            WalletData walletData = getWallet(deposit);

            DepositData depositData = new DepositData();

            depositData.setEventId(event.getId());
            depositData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            depositData.setDepositId(event.getSource());
            depositData.setSequenceId(event.getPayload().getSequence());
            LocalDateTime occurredAt = TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt());
            depositData.setEventOccuredAt(occurredAt);
            depositData.setCreatedAt(occurredAt);
            depositData.setEventType(DepositEventType.DEPOSIT_CREATED);
            depositData.setWalletId(deposit.getWallet());
            depositData.setSourceId(deposit.getSource());
            depositData.setDepositStatus(DepositStatus.pending);

            depositData.setIdentityId(walletData.getIdentityId());
            depositData.setPartyId(walletData.getPartyId());

            Cash cash = deposit.getBody();
            depositData.setAmount(cash.getAmount());
            depositData.setCurrencyCode(cash.getCurrency().getSymbolicCode());

            depositDao.save(depositData);
            log.info("Deposit have been saved, eventId={}, depositId={}", event.getId(), event.getSource());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private WalletData getWallet(Deposit deposit) throws DaoException {
        WalletData walletData = walletDao.get(deposit.getWallet());
        if (walletData == null) {
            throw new NotFoundException(String.format("Wallet with walletId='%s' not found", deposit.getWallet()));
        }
        if (walletData.getPartyId() == null) {
            throw new IllegalStateException(String.format("PartyId not found for wallet with walletId='%s'; it must be set for correct saving of DepositCreated", deposit.getWallet()));
        }
        return walletData;
    }

}
