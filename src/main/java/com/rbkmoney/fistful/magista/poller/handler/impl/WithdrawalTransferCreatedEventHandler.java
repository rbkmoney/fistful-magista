package com.rbkmoney.fistful.magista.poller.handler.impl;

import com.rbkmoney.fistful.cashflow.FinalCashFlowPosting;
import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.fistful.magista.exception.StorageException;
import com.rbkmoney.fistful.magista.poller.handler.WithdrawalEventHandler;
import com.rbkmoney.fistful.magista.util.CashFlowUtil;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WithdrawalTransferCreatedEventHandler implements WithdrawalEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalDao withdrawalDao;

    @Autowired
    public WithdrawalTransferCreatedEventHandler(WithdrawalDao withdrawalDao) {
        this.withdrawalDao = withdrawalDao;
    }

    @Override
    public boolean accept(Change change) {
        return change.isSetTransfer() && change.getTransfer().isSetPayload() && change.getTransfer().getPayload().isSetCreated()
                && change.getTransfer().getPayload().getCreated().isSetTransfer() && change.getTransfer().getPayload().getCreated().getTransfer().isSetCashflow();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            List<FinalCashFlowPosting> postings = change.getTransfer().getPayload().getCreated().getTransfer().getCashflow().getPostings();

            log.info("Trying to handle WithdrawalTransferCreated, eventId={}, withdrawalId={}", event.getId(), event.getSource());

            WithdrawalData withdrawalData = getWithdrawalData(event);

            withdrawalData.setFee(CashFlowUtil.getFistfulFee(postings));

            withdrawalDao.save(withdrawalData);

            log.info("WithdrawalTransferCreated has been saved, eventId={}, withdrawalId={}", event.getId(), event.getSource());
        } catch (DaoException ex) {
            throw new StorageException(ex);
        }
    }

    private WithdrawalData getWithdrawalData(SinkEvent event) throws DaoException {
        WithdrawalData withdrawalData = withdrawalDao.get(event.getSource());
        if (withdrawalData == null) {
            throw new NotFoundException(String.format("Withdrawal with withdrawalId='%s' not found", event.getSource()));
        }
        return withdrawalData;
    }

}
