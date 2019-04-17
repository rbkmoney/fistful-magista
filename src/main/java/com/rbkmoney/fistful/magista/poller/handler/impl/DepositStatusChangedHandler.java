package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.DepositEventHandler;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DepositStatusChangedHandler implements DepositEventHandler {

    private final DepositDao depositDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetStatusChanged();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start deposit status changed handling, eventId={}, depositId={}, status={}", event.getId(), event.getSource(), change.getStatusChanged());
            DepositData depositData = depositDao.get(event.getSource());

            depositData.setEventId(event.getId());
            depositData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            depositData.setDepositId(event.getSource());
            depositData.setSequenceId(event.getPayload().getSequence());
            depositData.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            depositData.setEventType(DepositEventType.DEPOSIT_STATUS_CHANGED);
            depositData.setDepositStatus(TBaseUtil.unionFieldToEnum(change.getStatusChanged(), DepositStatus.class));

            depositDao.save(depositData);
            log.info("Deposit status have been changed, eventId={}, depositId={}, status={}", event.getId(), event.getSource(), change.getStatusChanged());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}
