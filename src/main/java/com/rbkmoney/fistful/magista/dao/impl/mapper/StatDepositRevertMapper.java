package com.rbkmoney.fistful.magista.dao.impl.mapper;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.base.CurrencyRef;
import com.rbkmoney.fistful.fistful_stat.*;
import com.rbkmoney.fistful.magista.domain.enums.DepositRevertDataStatus;
import com.rbkmoney.fistful.magista.exception.NotFoundException;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import static com.rbkmoney.fistful.magista.domain.tables.DepositRevertData.DEPOSIT_REVERT_DATA;

public class StatDepositRevertMapper implements RowMapper<Map.Entry<Long, StatDepositRevert>> {

    @Override
    public Map.Entry<Long, StatDepositRevert> mapRow(ResultSet rs, int i) throws SQLException {
        String revertId = rs.getString(DEPOSIT_REVERT_DATA.REVERT_ID.getName());
        String walletId = rs.getString(DEPOSIT_REVERT_DATA.WALLET_ID.getName());
        String sourceId = rs.getString(DEPOSIT_REVERT_DATA.SOURCE_ID.getName());
        DepositRevertDataStatus status = TypeUtil.toEnumField(rs.getString(DEPOSIT_REVERT_DATA.STATUS.getName()),
                DepositRevertDataStatus.class);
        long amount = rs.getLong(DEPOSIT_REVERT_DATA.AMOUNT.getName());
        String currencyCode = rs.getString(DEPOSIT_REVERT_DATA.CURRENCY_CODE.getName());
        String createdAt = TypeUtil.temporalToString(rs.getObject(DEPOSIT_REVERT_DATA.CREATED_AT.getName(), LocalDateTime.class));
        long domainRevision = rs.getLong(DEPOSIT_REVERT_DATA.DOMAIN_REVISION.getName());
        long partyRevision = rs.getLong(DEPOSIT_REVERT_DATA.PARTY_REVISION.getName());
        String reason = rs.getString(DEPOSIT_REVERT_DATA.REASON.getName());
        String externalId = rs.getString(DEPOSIT_REVERT_DATA.EXTERNAL_ID.getName());

        StatDepositRevert statDepositRevert = new StatDepositRevert()
                .setId(revertId)
                .setWalletId(walletId)
                .setSourceId(sourceId)
                .setStatus(getStatus(status))
                .setBody(new Cash(amount, new CurrencyRef(currencyCode)))
                .setCreatedAt(createdAt)
                .setDomainRevision(domainRevision)
                .setPartyRevision(partyRevision)
                .setReason(reason)
                .setExternalId(externalId);

        return new SimpleEntry<>(rs.getLong(DEPOSIT_REVERT_DATA.ID.getName()), statDepositRevert);
    }

    private DepositRevertStatus getStatus(DepositRevertDataStatus status) {
        switch (status) {
            case pending:
                return DepositRevertStatus.pending(new DepositRevertPending());
            case succeeded:
                return DepositRevertStatus.succeeded(new DepositRevertSucceeded());
            case failed:
                return DepositRevertStatus.failed(new DepositRevertFailed(new Failure()));
            default:
                throw new NotFoundException(String.format("Deposit revert status '%s' not found", status.getLiteral()));
        }
    }
}
