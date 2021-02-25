package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.fistful_stat.*;
import com.rbkmoney.fistful.magista.dao.SearchDao;
import com.rbkmoney.fistful.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.fistful.magista.dao.impl.mapper.*;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.query.impl.WalletFunction;
import com.rbkmoney.fistful.magista.query.impl.WithdrawalFunction;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositAdjustmentParameters;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositParameters;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositRevertParameters;
import com.rbkmoney.fistful.magista.query.impl.parameters.IdentityParameters;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.Operator;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.rbkmoney.fistful.magista.domain.tables.ChallengeData.CHALLENGE_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.DepositAdjustmentData.DEPOSIT_ADJUSTMENT_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.DepositData.DEPOSIT_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.DepositRevertData.DEPOSIT_REVERT_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.IdentityData.IDENTITY_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.WalletData.WALLET_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.WithdrawalData.WITHDRAWAL_DATA;
import static org.jooq.Comparator.*;

@Component
public class SearchDaoImpl extends AbstractGenericDao implements SearchDao {

    private final StatWalletMapper statWalletMapper;
    private final StatWithdrawalMapper statWithdrawalMapper;
    private final StatDepositMapper statDepositMapper;
    private final StatIdentityMapper statIdentityMapper;
    private final StatDepositRevertMapper statDepositRevertMapper;
    private final StatDepositAdjustmentMapper statDepositAdjustmentMapper;

    public SearchDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        statWalletMapper = new StatWalletMapper();
        statWithdrawalMapper = new StatWithdrawalMapper();
        statDepositMapper = new StatDepositMapper();
        statIdentityMapper = new StatIdentityMapper();
        statDepositRevertMapper = new StatDepositRevertMapper();
        statDepositAdjustmentMapper = new StatDepositAdjustmentMapper();
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
                                                .addValue(DEPOSIT_DATA.PARTY_ID, parameters.getPartyId(), EQUALS)
                                                .addValue(DEPOSIT_DATA.DEPOSIT_ID, parameters.getDepositId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_DATA.IDENTITY_ID, parameters.getIdentityId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_DATA.WALLET_ID, parameters.getWalletId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_DATA.SOURCE_ID, parameters.getSourceId().orElse(null), EQUALS)
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

    @Override
    public Collection<Map.Entry<Long, StatIdentity>> getIdentities(IdentityParameters parameters, LocalDateTime fromTime, LocalDateTime toTime, Long fromId, int limit) throws DaoException {
        Query query = getDslContext()
                .select()
                .from(IDENTITY_DATA.leftJoin(CHALLENGE_DATA).on(IDENTITY_DATA.IDENTITY_ID.eq(CHALLENGE_DATA.IDENTITY_ID)))
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(IDENTITY_DATA.PARTY_ID, parameters.getPartyId(), EQUALS)
                                                .addValue(IDENTITY_DATA.PARTY_CONTRACT_ID, parameters.getPartyContractId().orElse(null), EQUALS)
                                                .addValue(IDENTITY_DATA.IDENTITY_ID, parameters.getIdentityId().orElse(null), EQUALS)
                                                .addValue(IDENTITY_DATA.IDENTITY_PROVIDER_ID, parameters.getIdentityProviderId().orElse(null), EQUALS)
                                                .addValue(IDENTITY_DATA.IDENTITY_CLASS_ID, parameters.getIdentityClassId().orElse(null), EQUALS)
                                                .addValue(IDENTITY_DATA.IDENTITY_EFFECTIVE_CHALLENGE_ID, parameters.getIdentityEffectiveChallengeId().orElse(null), EQUALS)
                                                .addValue(IDENTITY_DATA.IDENTITY_LEVEL_ID, parameters.getIdentityLevelId().orElse(null), EQUALS)
                                                .addValue(IDENTITY_DATA.ID, fromId, LESS)
                                                .addValue(CHALLENGE_DATA.CHALLENGE_ID, parameters.getChallengeId().orElse(null), EQUALS)
                                                .addValue(CHALLENGE_DATA.CHALLENGE_CLASS_ID, parameters.getChallengeClassId().orElse(null), EQUALS)
                                                .addValue(CHALLENGE_DATA.CHALLENGE_STATUS, parameters.getChallengeStatus().orElse(null), EQUALS)
                                                .addValue(CHALLENGE_DATA.CHALLENGE_RESOLUTION, parameters.getChallengeResolution().orElse(null), EQUALS)
                                                .addValue(CHALLENGE_DATA.CHALLENGE_VALID_UNTIL, TypeUtil.toLocalDateTime(parameters.getChallengeValidUntil()), GREATER_OR_EQUAL)

                                ),
                                IDENTITY_DATA.CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .orderBy(IDENTITY_DATA.ID.desc())
                .limit(limit);

        return fetch(query, statIdentityMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatDepositRevert>> getDepositsReverts(DepositRevertParameters parameters, LocalDateTime fromTime, LocalDateTime toTime, Long fromId, int limit) throws DaoException {
        Query query = getDslContext()
                .select()
                .from(DEPOSIT_REVERT_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(DEPOSIT_REVERT_DATA.PARTY_ID, parameters.getPartyId(), EQUALS)
                                                .addValue(DEPOSIT_REVERT_DATA.IDENTITY_ID, parameters.getIdentityId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_REVERT_DATA.SOURCE_ID, parameters.getSourceId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_REVERT_DATA.WALLET_ID, parameters.getWalletId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_REVERT_DATA.DEPOSIT_ID, parameters.getDepositId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_REVERT_DATA.REVERT_ID, parameters.getRevertId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_REVERT_DATA.AMOUNT, parameters.getAmountFrom().orElse(null), GREATER_OR_EQUAL)
                                                .addValue(DEPOSIT_REVERT_DATA.AMOUNT, parameters.getAmountTo().orElse(null), LESS_OR_EQUAL)
                                                .addValue(DEPOSIT_REVERT_DATA.CURRENCY_CODE, parameters.getCurrencyCode().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_REVERT_DATA.STATUS, parameters.getStatus().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_REVERT_DATA.ID, fromId, LESS)
                                ),
                                DEPOSIT_REVERT_DATA.CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .orderBy(DEPOSIT_REVERT_DATA.ID.desc())
                .limit(limit);

        return fetch(query, statDepositRevertMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatDepositAdjustment>> getDepositsAdjustments(DepositAdjustmentParameters parameters, LocalDateTime fromTime, LocalDateTime toTime, Long fromId, int limit) throws DaoException {
        Query query = getDslContext()
                .select()
                .from(DEPOSIT_ADJUSTMENT_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.PARTY_ID, parameters.getPartyId(), EQUALS)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.IDENTITY_ID, parameters.getIdentityId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.SOURCE_ID, parameters.getSourceId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.WALLET_ID, parameters.getWalletId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.DEPOSIT_ID, parameters.getDepositId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.ADJUSTMENT_ID, parameters.getAdjustmentId().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.AMOUNT, parameters.getAmountFrom().orElse(null), GREATER_OR_EQUAL)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.AMOUNT, parameters.getAmountTo().orElse(null), LESS_OR_EQUAL)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.CURRENCY_CODE, parameters.getCurrencyCode().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.STATUS, parameters.getDepositAdjustmentStatus().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.DEPOSIT_STATUS, parameters.getDepositStatus().orElse(null), EQUALS)
                                                .addValue(DEPOSIT_ADJUSTMENT_DATA.ID, fromId, LESS)
                                ),
                                DEPOSIT_ADJUSTMENT_DATA.CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .orderBy(DEPOSIT_ADJUSTMENT_DATA.ID.desc())
                .limit(limit);

        return fetch(query, statDepositAdjustmentMapper);
    }
}
