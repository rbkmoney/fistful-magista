package com.rbkmoney.fistful.magista.poller.handler.deposit;

import com.rbkmoney.fistful.cashflow.FinalCashFlowPosting;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositTransferStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.util.CashFlowUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DepositTransferCreatedHandler implements DepositEventHandler {

    private final DepositDao depositDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetTransfer()
                && change.getChange().getTransfer().isSetPayload()
                && change.getChange().getTransfer().getPayload().isSetCreated()
                && change.getChange().getTransfer().getPayload().getCreated().isSetTransfer()
                && change.getChange().getTransfer().getPayload().getCreated().getTransfer().isSetCashflow();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            List<FinalCashFlowPosting> postings = change
                    .getChange()
                    .getTransfer()
                    .getPayload()
                    .getCreated()
                    .getTransfer()
                    .getCashflow()
                    .getPostings();
            log.info("Start deposit transfer created handling: eventId={}, depositId={}, transferChange={}",
                    event.getEventId(), event.getSourceId(), change.getChange().getTransfer());

            DepositData depositData = depositDao.get(event.getSourceId());
            depositData.setEventId(event.getEventId());
            depositData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            depositData.setDepositId(event.getSourceId());
            depositData.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            depositData.setEventType(DepositEventType.DEPOSIT_TRANSFER_CREATED);
            depositData.setDepositTransferStatus(DepositTransferStatus.created);

            depositData.setFee(CashFlowUtil.getFistfulFee(postings));
            depositData.setProviderFee(CashFlowUtil.getFistfulProviderFee(postings));

            depositDao.save(depositData);
            log.info("Deposit transfer has been saved: eventId={}, depositId={}, transferChange={}",
                    event.getEventId(), event.getSourceId(), change.getChange().getTransfer());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}
