package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
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

    @Autowired
    public WithdrawalCreatedEventHandler(WithdrawalDao withdrawalDao) {
        this.withdrawalDao = withdrawalDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        Withdrawal withdrawal = change.getCreated();
        WithdrawalData withdrawalData = new WithdrawalData();
        withdrawalData.setWithdrawalId(event.getSource());
        withdrawalData.setSourceId(withdrawal.getSource());
        withdrawalData.setDestinationId(withdrawal.getDestination());
        withdrawalData.setAmount(withdrawal.getBody().getAmount());
        withdrawalData.setCurrencyCode(withdrawal.getBody().getCurrency().getSymbolicCode());

        WithdrawalEvent withdrawalEvent = new WithdrawalEvent();
        withdrawalEvent.setEventId(event.getPayload().getId());
        withdrawalEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawalEvent.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        withdrawalEvent.setEventType(WithdrawalEventType.WITHDRAWAL_CREATED);
        withdrawalEvent.setSequenceId(event.getSequence());
        withdrawalEvent.setWithdrawalId(event.getSource());
        withdrawalEvent.setWithdrawalStatus(WithdrawalStatus.pending);

        try {
            withdrawalDao.saveWithdrawalData(withdrawalData);
            withdrawalDao.saveWithdrawalEvent(withdrawalEvent);
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

}
