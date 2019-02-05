package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.Deposit;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.DepositEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DepositCreatedHandler implements DepositEventHandler {

    private final DepositDao depositDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start deposit created handling, eventId={}, depositId={}", event.getId(), event.getSource());
            Deposit deposit = new Deposit();

            deposit.setEventId(event.getId());
            deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            deposit.setDepositId(event.getSource());
            deposit.setSequenceId(event.getPayload().getSequence());
            deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            deposit.setEventType(DepositEventType.DEPOSIT_CREATED);
            deposit.setWalletId(change.getCreated().getWallet());
            deposit.setSourceId(change.getCreated().getSource());
            deposit.setDepositStatus(DepositStatus.pending);

            Cash cash = change.getCreated().getBody();
            deposit.setAmount(cash.getAmount());
            deposit.setCurrencyCode(cash.getCurrency().getSymbolicCode());

            depositDao.save(deposit);
            log.info("Deposit have been saved, eventId={}, depositId={}", event.getId(), event.getSource());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

}
