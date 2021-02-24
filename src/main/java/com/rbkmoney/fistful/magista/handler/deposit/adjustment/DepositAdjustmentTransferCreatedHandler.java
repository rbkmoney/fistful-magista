package com.rbkmoney.fistful.magista.handler.deposit.adjustment;

import com.rbkmoney.fistful.cashflow.FinalCashFlowPosting;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.magista.dao.DepositAdjustmentDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositAdjustmentDataEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositTransferStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositAdjustmentData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.handler.deposit.DepositEventHandler;
import com.rbkmoney.fistful.magista.util.CashFlowUtil;
import com.rbkmoney.fistful.transfer.Transfer;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositAdjustmentTransferCreatedHandler implements DepositEventHandler {

    private final DepositAdjustmentDao depositAdjustmentDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetAdjustment()
                && change.getChange().getAdjustment().getPayload().isSetTransfer()
                && change.getChange().getAdjustment().getPayload().getTransfer().getPayload().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            Transfer transfer = change.getChange().getAdjustment()
                    .getPayload().getTransfer()
                    .getPayload().getCreated().getTransfer();
            List<FinalCashFlowPosting> postings = transfer.getCashflow().getPostings();

            long eventId = event.getEventId();
            String depositId = event.getSourceId();
            String adjustmentId = change.getChange().getAdjustment().getId();
            LocalDateTime eventCreatedAt = TypeUtil.stringToLocalDateTime(event.getCreatedAt());
            LocalDateTime eventOccuredAt = TypeUtil.stringToLocalDateTime(change.getOccuredAt());
            DepositAdjustmentDataEventType eventType = DepositAdjustmentDataEventType.DEPOSIT_ADJUSTMENT_TRANSFER_CREATED;

            log.info("Start deposit adjustment transfer created handling, eventId={}, depositId={}, adjustmentId={}",
                    eventId, depositId, adjustmentId);

            DepositAdjustmentData depositAdjustmentData = depositAdjustmentDao.get(depositId, adjustmentId);
            initEventFields(depositAdjustmentData, eventId, eventCreatedAt, eventOccuredAt, eventType);
            depositAdjustmentData.setTransferStatus(DepositTransferStatus.created);
            depositAdjustmentData.setFee(CashFlowUtil.getFistfulFee(postings));
            depositAdjustmentData.setProviderFee(CashFlowUtil.getFistfulProviderFee(postings));

            depositAdjustmentDao.save(depositAdjustmentData).
                    ifPresentOrElse(
                            dbContractId -> log.info("Deposit adjustment transfer created has been saved, " +
                                    "eventId={}, depositId={}, adjustmentId={}", eventId, depositId, adjustmentId),
                            () -> log.info("Deposit adjustment transfer created has NOT been saved, " +
                                    "eventId={}, depositId={}, adjustmentId={}", eventId, depositId, adjustmentId));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}
