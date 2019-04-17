package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.domain.tables.records.WithdrawalDataRecord;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.fistful.magista.domain.tables.WithdrawalData.WITHDRAWAL_DATA;

@Component
public class WithdrawalDaoImpl extends AbstractGenericDao implements WithdrawalDao {

    private final RecordRowMapper<WithdrawalData> withdrawalRecordRowMapper;

    public WithdrawalDaoImpl(DataSource dataSource) {
        super(dataSource);
        withdrawalRecordRowMapper = new RecordRowMapper<>(WITHDRAWAL_DATA, WithdrawalData.class);
    }

    @Override
    public WithdrawalData get(String withdrawalId) throws DaoException {
        Query query = getDslContext()
                .selectFrom(WITHDRAWAL_DATA)
                .where(WITHDRAWAL_DATA.WITHDRAWAL_ID.eq(withdrawalId));

        return fetchOne(query, withdrawalRecordRowMapper);
    }

    @Override
    public long save(WithdrawalData withdrawal) throws DaoException {
        WithdrawalDataRecord withdrawalRecord = getDslContext().newRecord(WITHDRAWAL_DATA, withdrawal);

        Query query = getDslContext().insertInto(WITHDRAWAL_DATA)
                .set(withdrawalRecord)
                .onConflict(WITHDRAWAL_DATA.WITHDRAWAL_ID)
                .doUpdate()
                .set(withdrawalRecord)
                .returning(WITHDRAWAL_DATA.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(WITHDRAWAL_DATA.EVENT_ID)).from(WITHDRAWAL_DATA);
        return Optional.ofNullable(fetchOne(query, Long.class));
    }
}
