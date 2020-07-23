package com.rbkmoney.fistful.magista.poller.handler.deposit;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.deposit.Deposit;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositCreatedHandler implements DepositEventHandler {

    private final DepositDao depositDao;
    private final WalletDao walletDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated() && change.getChange().getCreated().isSetDeposit();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            Deposit deposit = change
                    .getChange()
                    .getCreated()
                    .getDeposit();
            log.info("Start deposit created handling: eventId={}, depositId={}", event.getEventId(), event.getSourceId());

            WalletData walletData = getWallet(deposit);

            DepositData depositData = new DepositData();
            depositData.setEventId(event.getEventId());
            depositData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            depositData.setDepositId(event.getSourceId());
            LocalDateTime occurredAt = TypeUtil.stringToLocalDateTime(change.getOccuredAt());
            depositData.setEventOccuredAt(occurredAt);
            depositData.setCreatedAt(occurredAt);
            depositData.setEventType(DepositEventType.DEPOSIT_CREATED);
            depositData.setWalletId(deposit.getWalletId());
            depositData.setSourceId(deposit.getSourceId());
            depositData.setDepositStatus(DepositStatus.pending);

            depositData.setIdentityId(walletData.getIdentityId());
            depositData.setPartyId(walletData.getPartyId());

            Cash cash = deposit.getBody();
            depositData.setAmount(cash.getAmount());
            depositData.setCurrencyCode(cash.getCurrency().getSymbolicCode());

            depositDao.save(depositData);
            log.info("Deposit has been saved: eventId={}, depositId={}", event.getEventId(), event.getSourceId());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private WalletData getWallet(Deposit deposit) throws DaoException {
        WalletData walletData = walletDao.get(deposit.getWalletId());
        if (walletData == null) {
            throw new NotFoundException(String.format("Wallet with walletId='%s' not found", deposit.getWalletId()));
        }

        if (walletData.getPartyId() == null) {
            throw new IllegalStateException(String.format("PartyId not found for wallet with walletId='%s'; " +
                    "it must be set for correct saving of DepositCreated", deposit.getWalletId()));
        }

        return walletData;
    }
}

