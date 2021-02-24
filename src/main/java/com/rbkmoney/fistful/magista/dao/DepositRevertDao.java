package com.rbkmoney.fistful.magista.dao;

import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositRevertData;
import com.rbkmoney.fistful.magista.exception.DaoException;

import java.util.Optional;

public interface DepositRevertDao extends GenericDao {

    Optional<Long> save(DepositRevertData depositRevertData) throws DaoException;

    DepositRevertData get(String depositId, String revertId) throws DaoException;

}
