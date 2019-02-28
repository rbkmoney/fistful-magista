package com.rbkmoney.fistful.magista.dao.impl.mapper;

import com.rbkmoney.fistful.fistful_stat.*;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import static com.rbkmoney.fistful.magista.domain.tables.Deposit.DEPOSIT;

public class StatDepositMapper implements RowMapper<Map.Entry<Long, StatDeposit>> {

    @Override
    public Map.Entry<Long, StatDeposit> mapRow(ResultSet rs, int i) throws SQLException {
        StatDeposit deposit = new StatDeposit();
        deposit.setId(rs.getString(DEPOSIT.DEPOSIT_ID.getName()));
        deposit.setCreatedAt(TypeUtil.temporalToString(rs.getObject(DEPOSIT.EVENT_CREATED_AT.getName(), LocalDateTime.class)));
        deposit.setIdentityId(rs.getString(DEPOSIT.IDENTITY_ID.getName()));
        deposit.setDestinationId(rs.getString(DEPOSIT.WALLET_ID.getName()));
        deposit.setSourceId(rs.getString(DEPOSIT.SOURCE_ID.getName()));
        deposit.setAmount(rs.getLong(DEPOSIT.AMOUNT.getName()));
        deposit.setFee(rs.getLong(DEPOSIT.FEE.getName()));
        deposit.setCurrencySymbolicCode(rs.getString(DEPOSIT.CURRENCY_CODE.getName()));
        DepositStatus depositStatus = TypeUtil.toEnumField(rs.getString(DEPOSIT.DEPOSIT_STATUS.getName()), DepositStatus.class);
        switch (depositStatus) {
            case succeeded:
                deposit.setStatus(com.rbkmoney.fistful.fistful_stat.DepositStatus.succeeded(new DepositSucceeded()));
                break;
            case pending:
                deposit.setStatus(com.rbkmoney.fistful.fistful_stat.DepositStatus.pending(new DepositPending()));
                break;
            case failed:
                deposit.setStatus(com.rbkmoney.fistful.fistful_stat.DepositStatus.failed(new DepositFailed(new Failure())));
                break;
            default:
                throw new NotFoundException(String.format("Deposit status '%s' not found", depositStatus.getLiteral()));
        }
        return new SimpleEntry<>(rs.getLong(DEPOSIT.ID.getName()), deposit);
    }
}
