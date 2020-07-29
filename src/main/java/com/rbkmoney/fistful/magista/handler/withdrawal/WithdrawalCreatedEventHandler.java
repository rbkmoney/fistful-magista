package com.rbkmoney.fistful.magista.handler.withdrawal;

import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.withdrawal.TimestampedChange;
import com.rbkmoney.fistful.withdrawal.Withdrawal;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalCreatedEventHandler implements WithdrawalEventHandler {

    private final WithdrawalDao withdrawalDao;
    private final WalletDao walletDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated()
                && change.getChange().getCreated().isSetWithdrawal();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            Withdrawal withdrawal = change.getChange().getCreated().getWithdrawal();
            log.info("Trying to handle WithdrawalCreated: eventId={}, withdrawalId={}", event.getEventId(), event.getSourceId());

            WalletData walletData = getWalletData(withdrawal);

            WithdrawalData withdrawalData = new WithdrawalData();
            withdrawalData.setWithdrawalId(event.getSourceId());
            withdrawalData.setWalletId(withdrawal.getWalletId());
            withdrawalData.setPartyId(walletData.getPartyId());
            withdrawalData.setIdentityId(walletData.getIdentityId());
            withdrawalData.setDestinationId(withdrawal.getDestinationId());
            withdrawalData.setAmount(withdrawal.getBody().getAmount());
            withdrawalData.setCurrencyCode(withdrawal.getBody().getCurrency().getSymbolicCode());
            withdrawalData.setEventId(event.getEventId());
            withdrawalData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            LocalDateTime occurredAt = TypeUtil.stringToLocalDateTime(change.getOccuredAt());
            withdrawalData.setCreatedAt(occurredAt);
            withdrawalData.setEventOccurredAt(occurredAt);
            withdrawalData.setExternalId(withdrawal.getExternalId());
            withdrawalData.setEventType(WithdrawalEventType.WITHDRAWAL_CREATED);
            withdrawalData.setWithdrawalStatus(WithdrawalStatus.pending);

            withdrawalDao.save(withdrawalData);
            log.info("WithdrawalCreated has been saved: eventId={}, withdrawalId={}", event.getEventId(), event.getSourceId());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

    private WalletData getWalletData(Withdrawal withdrawal) throws DaoException {
        WalletData walletData = walletDao.get(withdrawal.getWalletId());

        if (walletData == null) {
            throw new NotFoundException(String.format("WalletData with walletId='%s' not found", withdrawal.getWalletId()));
        }

        if (walletData.getPartyId() == null) {
            throw new IllegalStateException(String.format("PartyId not found for WalletData with walletId='%s'; " +
                    "it must be set for correct saving of WithdrawalCreated", withdrawal.getWalletId()));
        }

        return walletData;
    }
}
