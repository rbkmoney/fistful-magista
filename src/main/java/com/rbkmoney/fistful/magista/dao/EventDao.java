package com.rbkmoney.fistful.magista.dao;

import com.rbkmoney.fistful.magista.exception.DaoException;

import java.util.Optional;

public interface EventDao extends GenericDao {

    Optional<Long> getLastEventId() throws DaoException;

}
