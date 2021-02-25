package com.rbkmoney.fistful.magista.dao;

import com.rbkmoney.fistful.fistful_stat.*;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.query.impl.WalletFunction;
import com.rbkmoney.fistful.magista.query.impl.WithdrawalFunction;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositAdjustmentParameters;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositParameters;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositRevertParameters;
import com.rbkmoney.fistful.magista.query.impl.parameters.IdentityParameters;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface SearchDao {

    Collection<Map.Entry<Long, StatWallet>> getWallets(
            WalletFunction.WalletParameters parameters,
            Optional<Long> fromId,
            int limit
    ) throws DaoException;

    Collection<Map.Entry<Long, StatWithdrawal>> getWithdrawals(
            WithdrawalFunction.WithdrawalParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            Long fromId,
            int limit
    ) throws DaoException;

    Collection<Map.Entry<Long, StatDeposit>> getDeposits(
            DepositParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            Long fromId,
            int limit
    ) throws DaoException;

    Collection<Map.Entry<Long, StatIdentity>> getIdentities(
            IdentityParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            Long fromId,
            int limit
    ) throws DaoException;

    Collection<Map.Entry<Long, StatDepositRevert>> getDepositsReverts(
            DepositRevertParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            Long fromId,
            int limit
    ) throws DaoException;

    Collection<Map.Entry<Long, StatDepositAdjustment>> getDepositsAdjustments(
            DepositAdjustmentParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            Long fromId,
            int limit
    ) throws DaoException;

}
