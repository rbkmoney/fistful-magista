package com.rbkmoney.fistful.magista.dao;

import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositAdjustmentData;
import com.rbkmoney.fistful.magista.exception.DaoException;

import java.util.Optional;

public interface DepositAdjustmentDao extends GenericDao {

    Optional<Long> save(DepositAdjustmentData depositAdjustmentData) throws DaoException;

    DepositAdjustmentData get(String depositId, String adjustmentId) throws DaoException;

}
