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

import static com.rbkmoney.fistful.magista.domain.tables.DepositData.DEPOSIT_DATA;

public class StatDepositMapper implements RowMapper<Map.Entry<Long, StatDeposit>> {

    @Override
    public Map.Entry<Long, StatDeposit> mapRow(ResultSet rs, int i) throws SQLException {
        StatDeposit deposit = new StatDeposit();
        deposit.setId(rs.getString(DEPOSIT_DATA.DEPOSIT_ID.getName()));
        deposit.setCreatedAt(
                TypeUtil.temporalToString(rs.getObject(DEPOSIT_DATA.CREATED_AT.getName(), LocalDateTime.class)));
        deposit.setIdentityId(rs.getString(DEPOSIT_DATA.IDENTITY_ID.getName()));
        deposit.setDestinationId(rs.getString(DEPOSIT_DATA.WALLET_ID.getName()));
        deposit.setSourceId(rs.getString(DEPOSIT_DATA.SOURCE_ID.getName()));
        deposit.setAmount(rs.getLong(DEPOSIT_DATA.AMOUNT.getName()));
        deposit.setFee(rs.getLong(DEPOSIT_DATA.FEE.getName()));
        deposit.setCurrencySymbolicCode(rs.getString(DEPOSIT_DATA.CURRENCY_CODE.getName()));
        DepositStatus depositStatus =
                TypeUtil.toEnumField(rs.getString(DEPOSIT_DATA.DEPOSIT_STATUS.getName()), DepositStatus.class);
        deposit.setStatus(getDepositStatus(depositStatus));
        deposit.setRevertStatus(getRevertStatus(rs.getLong(DEPOSIT_DATA.AMOUNT.getName()),
                rs.getLong("REVERT_AMOUNT")));
        return new SimpleEntry<>(rs.getLong(DEPOSIT_DATA.ID.getName()), deposit);
    }

    private com.rbkmoney.fistful.fistful_stat.DepositStatus getDepositStatus(DepositStatus depositStatus) {
        return switch (depositStatus) {
            case succeeded -> com.rbkmoney.fistful.fistful_stat.DepositStatus.succeeded(new DepositSucceeded());
            case pending -> com.rbkmoney.fistful.fistful_stat.DepositStatus.pending(new DepositPending());
            case failed -> com.rbkmoney.fistful.fistful_stat.DepositStatus.failed(new DepositFailed(new Failure()));
            default -> throw new NotFoundException(
                    String.format("Deposit status '%s' not found", depositStatus.getLiteral()));
        };
    }

    private RevertStatus getRevertStatus(Long amount, Long revertAmount) {
        if (revertAmount == null || revertAmount == 0) {
            return RevertStatus.none;
        }
        if (revertAmount < amount) {
            return RevertStatus.partial;
        }
        if (revertAmount.equals(amount)) {
            return RevertStatus.full;
        }
        throw new RuntimeException("Wrong revert amount " + revertAmount);
    }
}
