package com.rbkmoney.fistful.magista.dao;

import com.rbkmoney.fistful.magista.exception.DaoException;

public interface FistfulDao<T> extends EventDao {

    long save(T object) throws DaoException;

    T get(String id) throws DaoException;

}
