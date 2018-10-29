package com.rbkmoney.fistful.magista.dao;

import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;

public interface IdentityDao extends GenericDao {

    IdentityData getIdentityData(String identityId) throws DaoException;

    long saveIdentityData(IdentityData identityData) throws DaoException;

    IdentityEvent getLastIdentityEvent(String identityId) throws DaoException;

    long saveIdentityEvent(IdentityEvent identityEvent) throws DaoException;

}
