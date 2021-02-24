package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.DepositAdjustmentDao;
import com.rbkmoney.fistful.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositAdjustmentData;
import com.rbkmoney.fistful.magista.domain.tables.records.DepositAdjustmentDataRecord;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.rbkmoney.fistful.magista.domain.tables.DepositAdjustmentData.DEPOSIT_ADJUSTMENT_DATA;

@Component
public class DepositAdjustmentDaoImpl extends AbstractGenericDao implements DepositAdjustmentDao {

    private final RowMapper<DepositAdjustmentData> depositRowMapper;

    @Autowired
    public DepositAdjustmentDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        depositRowMapper = new RecordRowMapper<>(DEPOSIT_ADJUSTMENT_DATA, DepositAdjustmentData.class);
    }

    @Override
    public Optional<Long> save(DepositAdjustmentData depositAdjustmentData) throws DaoException {
        DepositAdjustmentDataRecord record = getDslContext().newRecord(DEPOSIT_ADJUSTMENT_DATA, depositAdjustmentData);

        Query query = getDslContext().insertInto(DEPOSIT_ADJUSTMENT_DATA)
                .set(record)
                .onConflict(DEPOSIT_ADJUSTMENT_DATA.DEPOSIT_ID, DEPOSIT_ADJUSTMENT_DATA.ADJUSTMENT_ID)
                .doUpdate()
                .set(record)
                .returning(DEPOSIT_ADJUSTMENT_DATA.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public DepositAdjustmentData get(String depositId, String adjustmentId) throws DaoException {
        Query query = getDslContext().selectFrom(DEPOSIT_ADJUSTMENT_DATA)
                .where(DEPOSIT_ADJUSTMENT_DATA.DEPOSIT_ID.eq(depositId)
                        .and(DEPOSIT_ADJUSTMENT_DATA.ADJUSTMENT_ID.eq(adjustmentId)));

        return fetchOne(query, depositRowMapper);
    }
}
