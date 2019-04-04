package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.fistful.magista.domain.tables.pojos.Deposit;
import com.rbkmoney.fistful.magista.domain.tables.records.DepositRecord;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.fistful.magista.domain.tables.Deposit.DEPOSIT;

@Component
public class DepositDaoImpl extends AbstractGenericDao implements DepositDao {

    private final RowMapper<Deposit> depositRowMapper;

    @Autowired
    public DepositDaoImpl(DataSource dataSource) {
        super(dataSource);
        depositRowMapper = new RecordRowMapper<>(DEPOSIT, Deposit.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(DEPOSIT.EVENT_ID)).from(DEPOSIT);

        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Long save(Deposit deposit) throws DaoException {
        DepositRecord record = getDslContext().newRecord(DEPOSIT, deposit);
        Query query = getDslContext().insertInto(DEPOSIT).set(record).returning(DEPOSIT.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Deposit get(String depositId) throws DaoException {
        Query query = getDslContext().selectFrom(DEPOSIT)
                .where(
                        DEPOSIT.DEPOSIT_ID.eq(depositId)
                                .and(DEPOSIT.CURRENT)
                );

        return fetchOne(query, depositRowMapper);
    }

    @Override
    public void updateNotCurrent(String depositId) throws DaoException {
        Query query = getDslContext().update(DEPOSIT).set(DEPOSIT.CURRENT, false)
                .where(
                        DEPOSIT.DEPOSIT_ID.eq(depositId)
                                .and(DEPOSIT.CURRENT)
                );

        execute(query);
    }

}
