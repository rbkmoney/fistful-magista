package com.rbkmoney.fistful.magista.handler.deposit;

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
            Deposit deposit = change.getChange().getCreated().getDeposit();

            long eventId = event.getEventId();
            String depositId = event.getSourceId();
            LocalDateTime eventCreatedAt = TypeUtil.stringToLocalDateTime(event.getCreatedAt());
            LocalDateTime eventOccuredAt = TypeUtil.stringToLocalDateTime(change.getOccuredAt());

            log.info("Start deposit created handling: eventId={}, depositId={}", eventId, depositId);


            DepositData depositData = new DepositData();
            initEventFields(depositData, eventId, eventCreatedAt, eventOccuredAt, DepositEventType.DEPOSIT_CREATED);
            depositData.setSourceId(deposit.getSourceId());
            depositData.setWalletId(deposit.getWalletId());
            depositData.setDepositId(depositId);
            depositData.setDepositStatus(DepositStatus.pending);
            WalletData walletData = getWallet(deposit);
            Cash cash = deposit.getBody();
            depositData.setAmount(cash.getAmount());
            depositData.setCurrencyCode(cash.getCurrency().getSymbolicCode());
            depositData.setIdentityId(walletData.getIdentityId());
            depositData.setPartyId(walletData.getPartyId());
            depositData.setCreatedAt(eventOccuredAt);

            depositDao.save(depositData);

            log.info("Deposit has been saved: eventId={}, depositId={}", eventId, depositId);
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

