package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositTransferStatus;
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
public class DepositTransferStatusChangedHandler implements DepositEventHandler {

    private final DepositDao depositDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetTransfer() && change.getTransfer().isSetStatusChanged();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start deposit transfer status changed handling, eventId={}, depositId={}, transferChange={}", event.getId(), event.getSource(), change.getTransfer());
            DepositData depositData = depositDao.get(event.getSource());

            depositData.setEventId(event.getId());
            depositData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            depositData.setDepositId(event.getSource());
            depositData.setSequenceId(event.getPayload().getSequence());
            depositData.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            depositData.setEventType(DepositEventType.DEPOSIT_TRANSFER_STATUS_CHANGED);
            depositData.setDepositTransferStatus(TBaseUtil.unionFieldToEnum(change.getTransfer().getStatusChanged(), DepositTransferStatus.class));

            depositDao.save(depositData);

            log.info("Withdrawal deposit status have been changed, eventId={}, depositId={}, transferChange={}", event.getId(), event.getSource(), change.getTransfer());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}
