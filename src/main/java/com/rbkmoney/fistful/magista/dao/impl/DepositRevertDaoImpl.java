package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.DepositRevertDao;
import com.rbkmoney.fistful.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositRevertData;
import com.rbkmoney.fistful.magista.domain.tables.records.DepositRevertDataRecord;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.rbkmoney.fistful.magista.domain.tables.DepositRevertData.DEPOSIT_REVERT_DATA;


@Component
public class DepositRevertDaoImpl extends AbstractGenericDao implements DepositRevertDao {

    private final RowMapper<DepositRevertData> depositRowMapper;

    public DepositRevertDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        depositRowMapper = new RecordRowMapper<>(DEPOSIT_REVERT_DATA, DepositRevertData.class);
    }

    @Override
    public Optional<Long> save(DepositRevertData depositRevertData) throws DaoException {
        DepositRevertDataRecord record = getDslContext().newRecord(DEPOSIT_REVERT_DATA, depositRevertData);

        Query query = getDslContext().insertInto(DEPOSIT_REVERT_DATA)
                .set(record)
                .onConflict(DEPOSIT_REVERT_DATA.DEPOSIT_ID, DEPOSIT_REVERT_DATA.REVERT_ID)
                .doUpdate()
                .set(record)
                .returning(DEPOSIT_REVERT_DATA.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public DepositRevertData get(String depositId, String revertId) throws DaoException {
        Query query = getDslContext().selectFrom(DEPOSIT_REVERT_DATA)
                .where(DEPOSIT_REVERT_DATA.DEPOSIT_ID.eq(depositId)
                        .and(DEPOSIT_REVERT_DATA.REVERT_ID.eq(revertId)));

        return fetchOne(query, depositRowMapper);
    }
}
