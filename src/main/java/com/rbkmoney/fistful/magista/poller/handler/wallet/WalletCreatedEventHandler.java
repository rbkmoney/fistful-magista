package com.rbkmoney.fistful.magista.poller.handler.wallet;

import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.domain.enums.WalletEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.wallet.TimestampedChange;
import com.rbkmoney.fistful.wallet.Wallet;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletCreatedEventHandler implements WalletEventHandler {

    private final WalletDao walletDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        log.info("Trying to handle WalletCreated: eventId={}, walletId={}", event.getEventId(), event.getSourceId());
        Wallet wallet = change.getChange().getCreated();

        WalletData walletData = new WalletData();
        walletData.setWalletId(event.getSourceId());
        walletData.setWalletName(wallet.getName());
        walletData.setEventId(event.getEventId());
        walletData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        LocalDateTime occurredAt = TypeUtil.stringToLocalDateTime(change.getOccuredAt());
        walletData.setCreatedAt(occurredAt);
        walletData.setEventOccurredAt(occurredAt);
        walletData.setEventType(WalletEventType.WALLET_CREATED);
        walletData.setWalletId(event.getSourceId());

        try {
            walletDao.save(walletData);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }

        log.info("WalletCreated has been saved: eventId={}, walletId={}", event.getEventId(), event.getSourceId());
    }
}
