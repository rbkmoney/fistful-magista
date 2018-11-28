package com.rbkmoney.fistful.magista.dao.impl;


import com.rbkmoney.fistful.fistful_stat.StatWallet;
import com.rbkmoney.fistful.fistful_stat.StatWithdrawal;
import com.rbkmoney.fistful.magista.dao.SearchDao;
import com.rbkmoney.fistful.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.fistful.magista.dao.impl.mapper.StatWalletMapper;
import com.rbkmoney.fistful.magista.dao.impl.mapper.StatWithdrawalMapper;
import com.rbkmoney.fistful.magista.domain.enums.WithdrawalStatus;
import com.rbkmoney.fistful.magista.domain.tables.WalletEvent;
import com.rbkmoney.fistful.magista.domain.tables.WithdrawalEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.query.impl.WalletFunction;
import com.rbkmoney.fistful.magista.query.impl.WithdrawalFunction;
import com.rbkmoney.geck.common.util.TypeUtil;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.rbkmoney.fistful.magista.domain.tables.WalletData.WALLET_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.WalletEvent.WALLET_EVENT;
import static com.rbkmoney.fistful.magista.domain.tables.WithdrawalData.WITHDRAWAL_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.WithdrawalEvent.WITHDRAWAL_EVENT;
import static org.jooq.Comparator.*;

@Component
public class SearchDaoImpl extends AbstractGenericDao implements SearchDao {

    private final StatWalletMapper statWalletMapper;
    private final StatWithdrawalMapper statWithdrawalMapper;

    public SearchDaoImpl(DataSource ds) {
        super(ds);
        statWalletMapper = new StatWalletMapper();
        statWithdrawalMapper = new StatWithdrawalMapper();
    }

    @Override
    public Collection<Map.Entry<Long, StatWallet>> getWallets(
            WalletFunction.WalletParameters parameters,
            Optional<Long> fromId,
            int limit
    ) throws DaoException {
        WalletEvent walletEvent = WALLET_EVENT.as("wallet_event");

        Query query = getDslContext()
                .select()
                .from(WALLET_DATA)
                .join(
                        DSL.lateral(getDslContext()
                                .selectFrom(WALLET_EVENT)
                                .where(WALLET_DATA.WALLET_ID.eq(WALLET_EVENT.WALLET_ID))
                                .orderBy(WALLET_EVENT.ID.desc())
                                .limit(1)
                        ).as(walletEvent)
                ).on(
                        appendConditions(DSL.trueCondition(), Operator.AND, new ConditionParameterSource()
                                .addValue(WALLET_DATA.PARTY_ID, Optional.ofNullable(parameters.getPartyId())
                                        .map(UUID::fromString)
                                        .orElse(null), EQUALS)
                                .addValue(WALLET_DATA.IDENTITY_ID, parameters.getIdentityId(), EQUALS)
                                .addValue(walletEvent.CURRENCY_CODE, parameters.getCurrencyCode(), EQUALS)
                                .addValue(WALLET_DATA.ID, fromId.orElse(null), LESS))
                )
                .and(WALLET_DATA.PARTY_ID.isNotNull())
                .and(WALLET_DATA.IDENTITY_ID.isNotNull())
                .orderBy(walletEvent.EVENT_CREATED_AT.desc())
                .limit(limit);

        return fetch(query, statWalletMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatWithdrawal>> getWithdrawals(
            WithdrawalFunction.WithdrawalParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            int limit
    ) throws DaoException {

        WithdrawalEvent withdrawalEvent = WITHDRAWAL_EVENT.as("withdrawal_event");

        Query query = getDslContext()
                .select()
                .from(WITHDRAWAL_DATA)
                .join(
                        DSL.lateral(
                                getDslContext()
                                        .selectFrom(WITHDRAWAL_EVENT)
                                        .where(WITHDRAWAL_DATA.WITHDRAWAL_ID.eq(WITHDRAWAL_EVENT.WITHDRAWAL_ID))
                                        .orderBy(WITHDRAWAL_EVENT.ID.desc())
                                        .limit(1)

                        ).as(withdrawalEvent)
                ).on(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(WITHDRAWAL_DATA.PARTY_ID, Optional.ofNullable(parameters.getPartyId())
                                                        .map(UUID::fromString)
                                                        .orElse(null), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.WALLET_ID, parameters.getWalletId(), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.IDENTITY_ID, parameters.getIdentityId(), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.DESTINATION_ID, parameters.getDestinationId(), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.AMOUNT, parameters.getAmountFrom(), GREATER)
                                                .addValue(WITHDRAWAL_DATA.AMOUNT, parameters.getAmountTo(), LESS)
                                                .addValue(WITHDRAWAL_DATA.CURRENCY_CODE, parameters.getCurrencyCode(), EQUALS)
                                                .addValue(withdrawalEvent.WITHDRAWAL_STATUS, TypeUtil.toEnumField(parameters.getStatus(), WithdrawalStatus.class), EQUALS)
                                                .addValue(WITHDRAWAL_DATA.ID, fromId.orElse(null), LESS)),
                                withdrawalEvent.EVENT_CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .and(WITHDRAWAL_DATA.PARTY_ID.isNotNull())
                .and(WITHDRAWAL_DATA.IDENTITY_ID.isNotNull())
                .orderBy(withdrawalEvent.EVENT_CREATED_AT.desc()).limit(limit);

        return fetch(query, statWithdrawalMapper);
    }
}
