package com.rbkmoney.fistful.magista.dao.impl.mapper;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.base.CurrencyRef;
import com.rbkmoney.fistful.fistful_stat.*;
import com.rbkmoney.fistful.magista.domain.enums.DepositAdjustmentDataStatus;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;

import static com.rbkmoney.fistful.magista.domain.tables.DepositAdjustmentData.DEPOSIT_ADJUSTMENT_DATA;

public class StatDepositAdjustmentMapper implements RowMapper<Map.Entry<Long, StatDepositAdjustment>> {

    @Override
    public Map.Entry<Long, StatDepositAdjustment> mapRow(ResultSet rs, int i) throws SQLException {
        String adjustmentId = rs.getString(DEPOSIT_ADJUSTMENT_DATA.ADJUSTMENT_ID.getName());
        DepositAdjustmentDataStatus status =
                TypeUtil.toEnumField(rs.getString(DEPOSIT_ADJUSTMENT_DATA.STATUS.getName()),
                        DepositAdjustmentDataStatus.class);
        String createdAt = TypeUtil.temporalToString(
                rs.getObject(DEPOSIT_ADJUSTMENT_DATA.CREATED_AT.getName(), LocalDateTime.class));
        long domainRevision = rs.getLong(DEPOSIT_ADJUSTMENT_DATA.DOMAIN_REVISION.getName());
        long partyRevision = rs.getLong(DEPOSIT_ADJUSTMENT_DATA.PARTY_REVISION.getName());
        String externalId = rs.getString(DEPOSIT_ADJUSTMENT_DATA.EXTERNAL_ID.getName());
        String operationTimestamp = TypeUtil.temporalToString(
                rs.getObject(DEPOSIT_ADJUSTMENT_DATA.OPERATION_TIMESTAMP.getName(), LocalDateTime.class));
        String depositId = rs.getString(DEPOSIT_ADJUSTMENT_DATA.DEPOSIT_ID.getName());

        StatDepositAdjustment statDepositAdjustment = new StatDepositAdjustment()
                .setId(adjustmentId)
                .setStatus(getStatus(status))
                .setChangesPlan(getChangesPlan(rs))
                .setCreatedAt(createdAt)
                .setDomainRevision(domainRevision)
                .setPartyRevision(partyRevision)
                .setExternalId(externalId)
                .setOperationTimestamp(operationTimestamp)
                .setDepositId(depositId);

        return new AbstractMap.SimpleEntry<>(rs.getLong(DEPOSIT_ADJUSTMENT_DATA.ID.getName()), statDepositAdjustment);
    }

    private DepositAdjustmentStatus getStatus(DepositAdjustmentDataStatus status) {
        switch (status) {
            case pending:
                return DepositAdjustmentStatus.pending(new DepositAdjustmentPending());
            case succeeded:
                return DepositAdjustmentStatus.succeeded(new DepositAdjustmentSucceeded());
            default:
                throw new NotFoundException(
                        String.format("Deposit adjustment status '%s' not found", status.getLiteral()));
        }
    }

    private DepositAdjustmentStatusChangePlanStatus getStatus(DepositStatus status) {
        switch (status) {
            case pending:
                return DepositAdjustmentStatusChangePlanStatus.pending(new DepositAdjustmentStatusChangePlanPending());
            case succeeded:
                return DepositAdjustmentStatusChangePlanStatus
                        .succeeded(new DepositAdjustmentStatusChangePlanSucceeded());
            case failed:
                return DepositAdjustmentStatusChangePlanStatus
                        .failed(new DepositAdjustmentStatusChangePlanFailed(new Failure()));
            default:
                throw new NotFoundException(String.format("Deposit status '%s' not found", status.getLiteral()));
        }
    }

    private DepositAdjustmentChangesPlan getChangesPlan(ResultSet rs) throws SQLException {
        return new DepositAdjustmentChangesPlan()
                .setNewStatus(getDepositAdjustmentStatusChangePlan(rs))
                .setNewCash(getDepositAdjustmentCashChangePlan(rs));
    }

    private DepositAdjustmentStatusChangePlan getDepositAdjustmentStatusChangePlan(ResultSet rs) throws SQLException {
        DepositStatus depositStatus =
                TypeUtil.toEnumField(rs.getString(DEPOSIT_ADJUSTMENT_DATA.DEPOSIT_STATUS.getName()),
                        DepositStatus.class);

        if (depositStatus != null) {
            return new DepositAdjustmentStatusChangePlan()
                    .setNewStatus(getStatus(depositStatus));
        }

        return null;
    }

    private DepositAdjustmentCashChangePlan getDepositAdjustmentCashChangePlan(ResultSet rs) throws SQLException {
        Long amount = rs.getObject(DEPOSIT_ADJUSTMENT_DATA.AMOUNT.getName(), Long.class);
        Long fee = rs.getObject(DEPOSIT_ADJUSTMENT_DATA.FEE.getName(), Long.class);
        Long providerFee = rs.getObject(DEPOSIT_ADJUSTMENT_DATA.PROVIDER_FEE.getName(), Long.class);
        String currencyCode = rs.getString(DEPOSIT_ADJUSTMENT_DATA.CURRENCY_CODE.getName());

        if (amount != null && fee != null && providerFee != null && currencyCode != null) {
            return new DepositAdjustmentCashChangePlan()
                    .setAmount(new Cash(amount, new CurrencyRef(currencyCode)))
                    .setFee(new Cash(fee, new CurrencyRef(currencyCode)))
                    .setProviderFee(new Cash(providerFee, new CurrencyRef(currencyCode)));
        }

        return null;
    }
}
