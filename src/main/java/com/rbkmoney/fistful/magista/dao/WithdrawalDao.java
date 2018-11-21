package com.rbkmoney.fistful.magista.dao;

import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;

public interface WithdrawalDao extends GenericDao {

    WithdrawalData getWithdrawalData(String withdrawalId) throws DaoException;

    long saveWithdrawalData(WithdrawalData withdrawalData) throws DaoException;

    WithdrawalEvent getLastWithdrawalEvent(String withdrawalId) throws DaoException;

    long saveWithdrawalEvent(WithdrawalEvent withdrawalEvent) throws DaoException;

}
