package com.rbkmoney.fistful.magista.handler.deposit;

import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.deposit.status.Status;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositStatusChangedHandler implements DepositEventHandler {

    private final DepositDao depositDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetStatusChanged() && change.getChange().getStatusChanged().isSetStatus();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            Status status = change
                    .getChange()
                    .getStatusChanged()
                    .getStatus();
            log.info("Start deposit status changed handling: eventId={}, depositId={}, status={}",
                    event.getEventId(), event.getSourceId(), status);

            DepositData depositData = depositDao.get(event.getSourceId());
            depositData.setEventId(event.getEventId());
            depositData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            depositData.setDepositId(event.getSourceId());
            depositData.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            depositData.setEventType(DepositEventType.DEPOSIT_STATUS_CHANGED);
            depositData.setDepositStatus(TBaseUtil.unionFieldToEnum(status, DepositStatus.class));

            depositDao.save(depositData);
            log.info("Deposit status has been changed: eventId={}, depositId={}, status={}",
                    event.getEventId(), event.getSourceId(), status);
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}
