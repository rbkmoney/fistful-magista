package com.rbkmoney.fistful.magista.handler.deposit.adjustment;

import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.deposit.adjustment.Adjustment;
import com.rbkmoney.fistful.deposit.adjustment.CashFlowChangePlan;
import com.rbkmoney.fistful.deposit.status.Status;
import com.rbkmoney.fistful.magista.dao.DepositAdjustmentDao;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositAdjustmentDataEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositAdjustmentDataStatus;
import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositAdjustmentData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.handler.deposit.DepositEventHandler;
import com.rbkmoney.fistful.magista.util.CashFlowUtil;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositAdjustmentCreatedHandler implements DepositEventHandler {

    private final DepositAdjustmentDao depositAdjustmentDao;
    private final DepositDao depositDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetAdjustment()
                && change.getChange().getAdjustment().getPayload().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            var adjustment = change.getChange().getAdjustment().getPayload().getCreated().getAdjustment();

            long eventId = event.getEventId();
            String depositId = event.getSourceId();
            String adjustmentId = adjustment.getId();
            LocalDateTime eventCreatedAt = TypeUtil.stringToLocalDateTime(event.getCreatedAt());
            LocalDateTime eventOccuredAt = TypeUtil.stringToLocalDateTime(change.getOccuredAt());
            DepositAdjustmentDataEventType eventType = DepositAdjustmentDataEventType.DEPOSIT_ADJUSTMENT_CREATED;

            DepositData depositData = depositDao.get(depositId);

            log.info("Start deposit adjustment created handling, eventId={}, depositId={}, adjustmentId={}", eventId, depositId, adjustmentId);

            DepositAdjustmentData depositAdjustmentData = new DepositAdjustmentData();
            initEventFields(depositAdjustmentData, eventId, eventCreatedAt, eventOccuredAt, eventType);
            depositAdjustmentData.setSourceId(depositData.getSourceId());
            depositAdjustmentData.setWalletId(depositData.getWalletId());
            depositAdjustmentData.setDepositId(depositId);
            depositAdjustmentData.setAdjustmentId(adjustmentId);
            setCash(adjustment, depositAdjustmentData);
            depositAdjustmentData.setStatus(DepositAdjustmentDataStatus.pending);
            setDepositStatus(adjustment, depositAdjustmentData);
            depositAdjustmentData.setExternalId(adjustment.getExternalId());
            depositAdjustmentData.setPartyId(depositData.getPartyId());
            depositAdjustmentData.setIdentityId(depositData.getIdentityId());

            depositAdjustmentDao.save(depositAdjustmentData)
                    .ifPresentOrElse(
                            dbContractId -> log.info("Deposit adjustment created has been saved, " +
                                    "eventId={}, depositId={}, adjustmentId={}", eventId, depositId, adjustmentId),
                            () -> log.info("Deposit adjustment created has NOT been saved, " +
                                    "eventId={}, depositId={}, adjustmentId={}", eventId, depositId, adjustmentId));

            if (adjustment.getChangesPlan().isSetNewStatus()) {
                Status status = adjustment.getChangesPlan().getNewStatus().getNewStatus();

                initEventFields(depositData, eventId, eventCreatedAt, eventOccuredAt, DepositEventType.DEPOSIT_STATUS_CHANGED);
                depositData.setDepositStatus(TBaseUtil.unionFieldToEnum(status, DepositStatus.class));

                depositDao.save(depositData);

                log.info("Deposit status has been changed, eventId={}, depositId={}, status={}",
                        eventId, depositId, status);
            }
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private void setCash(Adjustment adjustment, DepositAdjustmentData depositAdjustmentData) {
        if (adjustment.getChangesPlan().isSetNewCashFlow()) {
            CashFlowChangePlan cashFlow = adjustment.getChangesPlan().getNewCashFlow();
            long amount = computeAmount(cashFlow);
            String currCode = getSymbolicCode(cashFlow);

            depositAdjustmentData.setAmount(amount);
            depositAdjustmentData.setFee(CashFlowUtil.getFistfulFee(cashFlow.getNewCashFlow().getPostings()));
            depositAdjustmentData.setProviderFee(CashFlowUtil.getFistfulProviderFee(cashFlow.getNewCashFlow().getPostings()));
            depositAdjustmentData.setCurrencyCode(currCode);
        }
    }

    private void setDepositStatus(Adjustment adjustment, DepositAdjustmentData depositAdjustmentData) {
        if (adjustment.getChangesPlan().isSetNewStatus()) {
            Status status = adjustment.getChangesPlan().getNewStatus().getNewStatus();
            depositAdjustmentData.setDepositStatus(TBaseUtil.unionFieldToEnum(status, DepositStatus.class));
        }
    }

    private String getSymbolicCode(CashFlowChangePlan cashFlow) {
        return cashFlow.getNewCashFlow().getPostings().get(0).getVolume().getCurrency().getSymbolicCode();
    }

    private long computeAmount(CashFlowChangePlan cashFlow) {
        Long oldAmount = CashFlowUtil.computeAmount(cashFlow.getOldCashFlowInverted().getPostings());
        Long newAmount = CashFlowUtil.computeAmount(cashFlow.getNewCashFlow().getPostings());
        return newAmount + oldAmount;
    }
}
