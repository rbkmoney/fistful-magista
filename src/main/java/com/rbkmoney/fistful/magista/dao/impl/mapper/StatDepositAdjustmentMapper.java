package com.rbkmoney.fistful.magista.dao.impl.mapper;

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
import java.util.Optional;

import static com.rbkmoney.fistful.magista.domain.tables.DepositAdjustmentData.DEPOSIT_ADJUSTMENT_DATA;

public class StatDepositAdjustmentMapper implements RowMapper<Map.Entry<Long, StatDepositAdjustment>> {

    @Override
    public Map.Entry<Long, StatDepositAdjustment> mapRow(ResultSet rs, int i) throws SQLException {
        String adjustmentId = rs.getString(DEPOSIT_ADJUSTMENT_DATA.ADJUSTMENT_ID.getName());
        DepositAdjustmentDataStatus status = TypeUtil.toEnumField(rs.getString(DEPOSIT_ADJUSTMENT_DATA.STATUS.getName()),
                DepositAdjustmentDataStatus.class);
        DepositAdjustmentChangesPlan changesPlan = getChangesPlan(rs);
        String createdAt = TypeUtil.temporalToString(rs.getObject(DEPOSIT_ADJUSTMENT_DATA.CREATED_AT.getName(), LocalDateTime.class));
        long domainRevision = rs.getLong(DEPOSIT_ADJUSTMENT_DATA.DOMAIN_REVISION.getName());
        long partyRevision = rs.getLong(DEPOSIT_ADJUSTMENT_DATA.PARTY_REVISION.getName());
        String externalId = rs.getString(DEPOSIT_ADJUSTMENT_DATA.EXTERNAL_ID.getName());
        String operationTimestamp = TypeUtil.temporalToString(rs.getObject(DEPOSIT_ADJUSTMENT_DATA.OPERATION_TIMESTAMP.getName(), LocalDateTime.class));

        StatDepositAdjustment statDepositAdjustment = new StatDepositAdjustment();
        statDepositAdjustment.setId(adjustmentId);
        statDepositAdjustment.setStatus(getStatus(status));
        statDepositAdjustment.setChangesPlan(changesPlan);
        statDepositAdjustment.setCreatedAt(createdAt);
        statDepositAdjustment.setDomainRevision(domainRevision);
        statDepositAdjustment.setPartyRevision(partyRevision);
        statDepositAdjustment.setExternalId(externalId);
        statDepositAdjustment.setOperationTimestamp(operationTimestamp);

        return new AbstractMap.SimpleEntry<>(rs.getLong(DEPOSIT_ADJUSTMENT_DATA.ID.getName()), statDepositAdjustment);
    }

    private DepositAdjustmentStatus getStatus(DepositAdjustmentDataStatus status) {
        switch (status) {
            case pending:
                return DepositAdjustmentStatus.pending(new DepositAdjustmentPending());
            case succeeded:
                return DepositAdjustmentStatus.succeeded(new DepositAdjustmentSucceeded());
            default:
                throw new NotFoundException(String.format("Deposit adjustment status '%s' not found", status.getLiteral()));
        }
    }

    private DepositAdjustmentChangesPlan getChangesPlan(ResultSet rs) throws SQLException {
        DepositStatus depositStatus = TypeUtil.toEnumField(rs.getString(DEPOSIT_ADJUSTMENT_DATA.DEPOSIT_STATUS.getName()),
                DepositStatus.class);
        Optional<DepositAdjustmentStatusChangePlan> statusChangePlan = Optional.ofNullable(depositStatus)
                .map(this::getStatus)
                .map(ds -> new DepositAdjustmentStatusChangePlan().setNewStatus(ds));

        DepositAdjustmentChangesPlan depositAdjustmentChangesPlan = new DepositAdjustmentChangesPlan();
        statusChangePlan.ifPresent(depositAdjustmentChangesPlan::setNewStatus);
        return depositAdjustmentChangesPlan;
    }

    private DepositAdjustmentStatusChangePlanStatus getStatus(DepositStatus status) {
        switch (status) {
            case pending:
                return DepositAdjustmentStatusChangePlanStatus.pending(new DepositAdjustmentStatusChangePlanPending());
            case succeeded:
                return DepositAdjustmentStatusChangePlanStatus.succeeded(new DepositAdjustmentStatusChangePlanSucceeded());
            case failed:
                return DepositAdjustmentStatusChangePlanStatus.failed(new DepositAdjustmentStatusChangePlanFailed(new Failure()));
            default:
                throw new NotFoundException(String.format("Deposit status '%s' not found", status.getLiteral()));
        }
    }
}
