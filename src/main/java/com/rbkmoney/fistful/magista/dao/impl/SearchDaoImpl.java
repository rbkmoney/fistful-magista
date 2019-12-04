package com.rbkmoney.fistful.magista.dao.impl;


import com.rbkmoney.fistful.fistful_stat.StatDeposit;
import com.rbkmoney.fistful.fistful_stat.StatWallet;
import com.rbkmoney.fistful.fistful_stat.StatWithdrawal;
import com.rbkmoney.fistful.magista.dao.SearchDao;
import com.rbkmoney.fistful.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.fistful.magista.dao.impl.mapper.StatDepositMapper;
import com.rbkmoney.fistful.magista.dao.impl.mapper.StatWalletMapper;
import com.rbkmoney.fistful.magista.dao.impl.mapper.StatWithdrawalMapper;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.query.impl.WalletFunction;
import com.rbkmoney.fistful.magista.query.impl.WithdrawalFunction;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositParameters;
import org.jooq.Operator;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.rbkmoney.fistful.magista.domain.tables.DepositData.DEPOSIT_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.WalletData.WALLET_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.WithdrawalData.WITHDRAWAL_DATA;
import static org.jooq.Comparator.*;

@Component
public class SearchDaoImpl extends AbstractGenericDao implements SearchDao {

    private final StatWalletMapper statWalletMapper;
    private final StatWithdrawalMapper statWithdrawalMapper;
    private final StatDepositMapper statDepositMapper;

    public SearchDaoImpl(DataSource ds) {
        super(ds);
        statWalletMapper = new StatWalletMapper();
        statWithdrawalMapper = new StatWithdrawalMapper();
        statDepositMapper = new StatDepositMapper();
    }

    @Override
    public Collection<Map.Entry<Long, StatWallet>> getWallets(
            WalletFunction.WalletParameters parameters,
            Optional<Long> fromId,
            int limit
    ) throws DaoException {
        Query query = getDslContext()
                .select()
                .from(WALLET_DATA)
                .where(
                        appendConditions(DSL.trueCondition(), Operator.AND, new ConditionParameterSource()
                                .addValue(WALLET_DATA.PARTY_ID, Optional.ofNullable(parameters.getPartyId())
                                        .map(UUID::fromString)
                                        .orElse(null), EQUALS)
                                .addValue(WALLET_DATA.IDENTITY_ID, parameters.getIdentityId(), EQUALS)
                                .addValue(WALLET_DATA.CURRENCY_CODE, parameters.getCurrencyCode(), EQUALS)
                                .addValue(WALLET_DATA.ID, fromId.orElse(null), LESS))
                )
                .and(WALLET_DATA.PARTY_ID.isNotNull())
                .and(WALLET_DATA.IDENTITY_ID.isNotNull())
                .orderBy(WALLET_DATA.ID.desc())
                .limit(limit);

        return fetch(query, statWalletMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatWithdrawal>> getWithdrawals(
            WithdrawalFunction.WithdrawalParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            Long fromId,
            int limit
    ) throws DaoException {
        Query query = getDslContext()
                .select()
                .from(WITHDRAWAL_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(WITHDRAWAL_DATA.PARTY_ID, Optional.ofNullable(parameters.getPartyId())
                                                        .map(UUID::fromString)
                                                        .orElse(null), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.WALLET_ID, parameters.getWalletId(), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.WITHDRAWAL_ID, parameters.getWithdrawalId(), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.IDENTITY_ID, parameters.getIdentityId(), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.DESTINATION_ID, parameters.getDestinationId(), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.AMOUNT, parameters.getAmountFrom(), GREATER)
                                                .addValue(WITHDRAWAL_DATA.AMOUNT, parameters.getAmountTo(), LESS)
                                                .addValue(WITHDRAWAL_DATA.CURRENCY_CODE, parameters.getCurrencyCode(), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.WITHDRAWAL_STATUS, parameters.getStatus(), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.EXTERNAL_ID, parameters.getExternalId(), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.ID, fromId, LESS)),
                                WITHDRAWAL_DATA.CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .and(WITHDRAWAL_DATA.PARTY_ID.isNotNull())
                .and(WITHDRAWAL_DATA.IDENTITY_ID.isNotNull())
                .orderBy(WITHDRAWAL_DATA.ID.desc()).limit(limit);

        return fetch(query, statWithdrawalMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatDeposit>> getDeposits(
            DepositParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            Long fromId,
            int limit
    ) throws DaoException {
        Query query = getDslContext()
                .select()
                .from(DEPOSIT_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(DEPOSIT_DATA.PARTY_ID,
                                                        Optional.ofNullable(parameters.getPartyId()).orElse(null), EQUALS)
                                                .addValue(DEPOSIT_DATA.DEPOSIT_ID, parameters.getDepositId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_DATA.IDENTITY_ID, parameters.getIdentityId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_DATA.WALLET_ID, parameters.getWalletId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_DATA.SOURCE_ID, parameters.getSourceId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_DATA.PARTY_ID, parameters.getPartyId(), EQUALS)
                                                .addValue(DEPOSIT_DATA.AMOUNT, parameters.getAmountFrom().orElse(null), GREATER_OR_EQUAL)
                                                .addValue(DEPOSIT_DATA.AMOUNT, parameters.getAmountTo().orElse(null), LESS_OR_EQUAL)
                                                .addValue(DEPOSIT_DATA.CURRENCY_CODE, parameters.getCurrencyCode().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_DATA.DEPOSIT_STATUS, parameters.getStatus().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_DATA.ID, fromId, LESS)
                                ),
                                DEPOSIT_DATA.CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .orderBy(DEPOSIT_DATA.ID.desc())
                .limit(limit);

        return fetch(query, statDepositMapper);
    }
}
