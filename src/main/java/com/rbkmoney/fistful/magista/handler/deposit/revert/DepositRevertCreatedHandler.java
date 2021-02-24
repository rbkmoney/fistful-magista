package com.rbkmoney.fistful.magista.handler.deposit.revert;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.dao.DepositRevertDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositRevertDataEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositRevertDataStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositRevertData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.handler.deposit.DepositEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositRevertCreatedHandler implements DepositEventHandler {

    private final DepositRevertDao depositRevertDao;
    private final DepositDao depositDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetRevert()
                && change.getChange().getRevert().getPayload().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            var revert = change.getChange().getRevert().getPayload().getCreated().getRevert();

            long eventId = event.getEventId();
            String depositId = event.getSourceId();
            String revertId = revert.getId();
            LocalDateTime eventCreatedAt = TypeUtil.stringToLocalDateTime(event.getCreatedAt());
            LocalDateTime eventOccuredAt = TypeUtil.stringToLocalDateTime(change.getOccuredAt());
            DepositRevertDataEventType eventType = DepositRevertDataEventType.DEPOSIT_REVERT_CREATED;
            Cash cash = revert.getBody();

            DepositData depositData = depositDao.get(depositId);

            log.info("Start deposit revert created handling, eventId={}, depositId={}, revertId={}",
                    eventId, depositId, revertId);

            DepositRevertData depositRevertData = new DepositRevertData();
            initEventFields(depositRevertData, eventId, eventCreatedAt, eventOccuredAt, eventType);
            depositRevertData.setSourceId(depositData.getSourceId());
            depositRevertData.setWalletId(depositData.getWalletId());
            depositRevertData.setDepositId(depositId);
            depositRevertData.setRevertId(revertId);
            depositRevertData.setAmount(cash.getAmount());
            depositRevertData.setCurrencyCode(cash.getCurrency().getSymbolicCode());
            depositRevertData.setStatus(DepositRevertDataStatus.pending);
            depositRevertData.setExternalId(revert.getExternalId());
            depositRevertData.setReason(revert.getReason());
            depositRevertData.setExternalId(revert.getExternalId());
            depositRevertData.setPartyId(depositData.getPartyId());
            depositRevertData.setIdentityId(depositData.getIdentityId());

            depositRevertDao.save(depositRevertData).ifPresentOrElse(
                    dbContractId -> log.info("Deposit revert created has been saved, eventId={}, depositId={}, revertId={}",
                            eventId, depositId, revertId),
                    () -> log.info("Deposit revert created has NOT been saved, eventId={}, depositId={}, revertId={}",
                            eventId, depositId, revertId));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}
