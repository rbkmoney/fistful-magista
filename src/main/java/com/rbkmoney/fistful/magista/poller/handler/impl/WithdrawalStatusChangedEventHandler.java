package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WithdrawalEventHandler;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.fistful.withdrawal.status.Status;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalStatusChangedEventHandler implements WithdrawalEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalDao withdrawalDao;

    @Autowired
    public WithdrawalStatusChangedEventHandler(WithdrawalDao withdrawalDao) {
        this.withdrawalDao = withdrawalDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetStatusChanged() && change.getStatusChanged().isSetStatus();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            Status status = change.getStatusChanged().getStatus();

            log.info("Trying to handle WithdrawalStatusChanged, eventId={}, withdrawalId={}", event.getId(), event.getSource());

            WithdrawalData withdrawalData = getWithdrawalData(event);

            withdrawalData.setEventId(event.getId());
            withdrawalData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            withdrawalData.setEventOccurredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            withdrawalData.setEventType(WithdrawalEventType.WITHDRAWAL_STATUS_CHANGED);
            withdrawalData.setSequenceId(event.getPayload().getSequence());
            withdrawalData.setWithdrawalStatus(TBaseUtil.unionFieldToEnum(status, WithdrawalStatus.class));

            withdrawalDao.save(withdrawalData);

            log.info("WithdrawalStatusChanged has been saved, eventId={}, withdrawalId={}", event.getId(), event.getSource());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

    private WithdrawalData getWithdrawalData(SinkEvent event) throws DaoException {
        WithdrawalData withdrawalData = withdrawalDao.get(event.getSource());
        if (withdrawalData == null) {
            throw new NotFoundException(String.format("WithdrawalEvent with withdrawalId='%s' not found", event.getSource()));
        }
        return withdrawalData;
    }

}
