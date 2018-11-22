package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalEvent;
import com.rbkmoney.fistful.magista.domain.tables.records.WithdrawalDataRecord;
import com.rbkmoney.fistful.magista.domain.tables.records.WithdrawalEventRecord;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.Optional;

import static com.rbkmoney.fistful.magista.domain.tables.WithdrawalData.WITHDRAWAL_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.WithdrawalEvent.WITHDRAWAL_EVENT;

@Component
public class WithdrawalDaoImpl extends AbstractGenericDao implements WithdrawalDao {

    private final RecordRowMapper<WithdrawalData> withdrawalDataRecordRowMapper;
    private final RecordRowMapper<WithdrawalEvent> withdrawalEventRecordRowMapper;

    @Autowired
    public WithdrawalDaoImpl(DataSource dataSource) {
        super(dataSource);
        withdrawalDataRecordRowMapper = new RecordRowMapper<>(WITHDRAWAL_DATA, WithdrawalData.class);
        withdrawalEventRecordRowMapper = new RecordRowMapper<>(WITHDRAWAL_EVENT, WithdrawalEvent.class);
    }

    @Override
    public WithdrawalData getWithdrawalData(String withdrawalId) throws DaoException {
        Query query = getDslContext().selectFrom(WITHDRAWAL_DATA)
                .where(WITHDRAWAL_DATA.WITHDRAWAL_ID.eq(withdrawalId));

        return fetchOne(query, withdrawalDataRecordRowMapper);
    }

    @Override
    public long saveWithdrawalData(WithdrawalData withdrawalData) throws DaoException {
        WithdrawalDataRecord withdrawalDataRecord = getDslContext().newRecord(WITHDRAWAL_DATA, withdrawalData);

        Query query = getDslContext().insertInto(WITHDRAWAL_DATA)
                .set(withdrawalDataRecord)
                .onConflict(WITHDRAWAL_DATA.WITHDRAWAL_ID)
                .doNothing()
                .returning(WITHDRAWAL_DATA.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public WithdrawalEvent getLastWithdrawalEvent(String withdrawalId) throws DaoException {
        Query query = getDslContext()
                .selectFrom(WITHDRAWAL_EVENT)
                .where(WITHDRAWAL_EVENT.WITHDRAWAL_ID.eq(withdrawalId))
                .orderBy(WITHDRAWAL_EVENT.ID.desc())
                .limit(1);

        return fetchOne(query, withdrawalEventRecordRowMapper);
    }

    @Override
    public long saveWithdrawalEvent(WithdrawalEvent withdrawalEvent) throws DaoException {
        WithdrawalEventRecord withdrawalEventRecord = getDslContext().newRecord(WITHDRAWAL_EVENT, withdrawalEvent);

        Query query = getDslContext().insertInto(WITHDRAWAL_EVENT)
                .set(withdrawalEventRecord)
                .onConflict(WITHDRAWAL_EVENT.EVENT_ID, WITHDRAWAL_EVENT.SEQUENCE_ID)
                .doUpdate()
                .set(withdrawalEventRecord)
                .returning(WITHDRAWAL_EVENT.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(WITHDRAWAL_EVENT.EVENT_ID)).from(WITHDRAWAL_EVENT);
        return Optional.ofNullable(fetchOne(query, Long.class));
    }
}
