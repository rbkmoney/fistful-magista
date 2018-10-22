package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityEvent;
import com.rbkmoney.fistful.magista.domain.tables.records.IdentityDataRecord;
import com.rbkmoney.fistful.magista.domain.tables.records.IdentityEventRecord;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.fistful.magista.domain.tables.IdentityData.IDENTITY_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.IdentityEvent.IDENTITY_EVENT;

@Component
public class IdentityDaoImpl extends AbstractGenericDao implements IdentityDao {

    private final RecordRowMapper<IdentityData> identityDataRecordRowMapper;
    private final RecordRowMapper<IdentityEvent> identityEventRecordRowMapper;

    @Autowired
    public IdentityDaoImpl(DataSource dataSource) {
        super(dataSource);
        identityDataRecordRowMapper = new RecordRowMapper<>(IDENTITY_DATA, IdentityData.class);
        identityEventRecordRowMapper = new RecordRowMapper<>(IDENTITY_EVENT, IdentityEvent.class);
    }

    @Override
    public IdentityData getIdentityData(String identityId) throws DaoException {
        Query query = getDslContext().selectFrom(IDENTITY_DATA)
                .where(IDENTITY_DATA.IDENTITY_ID.eq(identityId));

        return fetchOne(query, identityDataRecordRowMapper);
    }

    @Override
    public long saveIdentityData(IdentityData identityData) throws DaoException {
        IdentityDataRecord identityDataRecord = getDslContext().newRecord(IDENTITY_DATA, identityData);

        Query query = getDslContext().insertInto(IDENTITY_DATA)
                .set(identityDataRecord)
                .onConflict(IDENTITY_DATA.IDENTITY_ID)
                .doNothing()
                .returning(IDENTITY_DATA.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public IdentityEvent getLastIdentityEvent(String identityId) throws DaoException {
        Query query = getDslContext()
                .selectFrom(IDENTITY_EVENT)
                .where(IDENTITY_EVENT.IDENTITY_ID.eq(identityId))
                .orderBy(IDENTITY_EVENT.ID.desc())
                .limit(1);

        return fetchOne(query, identityEventRecordRowMapper);
    }

    @Override
    public long saveIdentityEvent(IdentityEvent identityEvent) throws DaoException {
        IdentityEventRecord identityEventRecord = getDslContext().newRecord(IDENTITY_EVENT, identityEvent);

        Query query = getDslContext().insertInto(IDENTITY_EVENT)
                .set(identityEventRecord)
                .onConflict(IDENTITY_EVENT.EVENT_ID, IDENTITY_EVENT.SEQUENCE_ID)
                .doUpdate()
                .set(identityEventRecord)
                .returning(IDENTITY_EVENT.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

}
