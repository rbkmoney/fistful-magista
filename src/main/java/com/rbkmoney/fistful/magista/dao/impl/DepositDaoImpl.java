package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.domain.tables.records.DepositDataRecord;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.fistful.magista.domain.tables.DepositData.DEPOSIT_DATA;

@Component
public class DepositDaoImpl extends AbstractGenericDao implements DepositDao {

    private final RowMapper<DepositData> depositRowMapper;

    @Autowired
    public DepositDaoImpl(DataSource dataSource) {
        super(dataSource);
        depositRowMapper = new RecordRowMapper<>(DEPOSIT_DATA, DepositData.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(DEPOSIT_DATA.EVENT_ID)).from(DEPOSIT_DATA);

        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public long save(DepositData deposit) throws DaoException {
        DepositDataRecord depositRecord = getDslContext().newRecord(DEPOSIT_DATA, deposit);

        Query query = getDslContext().insertInto(DEPOSIT_DATA)
                .set(depositRecord)
                .onConflict(DEPOSIT_DATA.DEPOSIT_ID)
                .doUpdate()
                .set(depositRecord)
                .returning(DEPOSIT_DATA.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public DepositData get(String depositId) throws DaoException {
        Query query = getDslContext().selectFrom(DEPOSIT_DATA)
                .where(DEPOSIT_DATA.DEPOSIT_ID.eq(depositId));

        return fetchOne(query, depositRowMapper);
    }

}
