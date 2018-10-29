package com.rbkmoney.fistful.magista.dao;

import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;

public interface WalletDao extends GenericDao {

    WalletData getWalletData(String walletId) throws DaoException;

    long saveWalletData(WalletData walletData) throws DaoException;

    WalletEvent getLastWalletEvent(String walletId) throws DaoException;

    long saveWalletEvent(WalletEvent walletEvent) throws DaoException;

}
