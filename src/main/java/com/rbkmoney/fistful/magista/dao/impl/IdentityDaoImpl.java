package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.domain.tables.records.ChallengeDataRecord;
import com.rbkmoney.fistful.magista.domain.tables.records.IdentityDataRecord;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.fistful.magista.domain.tables.ChallengeData.CHALLENGE_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.IdentityData.IDENTITY_DATA;

@Component
public class IdentityDaoImpl extends AbstractGenericDao implements IdentityDao {

    private final RecordRowMapper<IdentityData> identityRecordRowMapper;
    private final RecordRowMapper<ChallengeData> challengeRecordRowMapper;

    @Autowired
    public IdentityDaoImpl(DataSource dataSource) {
        super(dataSource);
        identityRecordRowMapper = new RecordRowMapper<>(IDENTITY_DATA, IdentityData.class);
        challengeRecordRowMapper = new RecordRowMapper<>(CHALLENGE_DATA, ChallengeData.class);
    }

    @Override
    public IdentityData get(String identityId) throws DaoException {
        Query query = getDslContext().selectFrom(IDENTITY_DATA)
                .where(IDENTITY_DATA.IDENTITY_ID.eq(identityId));

        return fetchOne(query, identityRecordRowMapper);
    }

    @Override
    public ChallengeData get(String identityId, String challengeId) throws DaoException {
        Query query = getDslContext()
                .selectFrom(CHALLENGE_DATA)
                .where(
                        CHALLENGE_DATA.IDENTITY_ID.eq(identityId)
                                .and(CHALLENGE_DATA.CHALLENGE_ID.eq(challengeId))
                );

        return fetchOne(query, challengeRecordRowMapper);
    }

    @Override
    public long save(IdentityData identity) throws DaoException {
        IdentityDataRecord identityRecord = getDslContext().newRecord(IDENTITY_DATA, identity);

        Query query = getDslContext().insertInto(IDENTITY_DATA)
                .set(identityRecord)
                .onConflict(IDENTITY_DATA.IDENTITY_ID)
                .doUpdate()
                .set(identityRecord)
                .returning(IDENTITY_DATA.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public long save(ChallengeData challenge) throws DaoException {
        ChallengeDataRecord challengeRecord = getDslContext().newRecord(CHALLENGE_DATA, challenge);

        Query query = getDslContext().insertInto(CHALLENGE_DATA)
                .set(challengeRecord)
                .onConflict(CHALLENGE_DATA.IDENTITY_ID, CHALLENGE_DATA.CHALLENGE_ID)
                .doUpdate()
                .set(challengeRecord)
                .returning(CHALLENGE_DATA.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(DSL.field("event_id"))).from(
                getDslContext().select(DSL.max(IDENTITY_DATA.EVENT_ID).as("event_id")).from(IDENTITY_DATA)
                        .unionAll(getDslContext().select(DSL.max(CHALLENGE_DATA.EVENT_ID).as("event_id"))
                                .from(CHALLENGE_DATA))
        );
        return Optional.ofNullable(fetchOne(query, Long.class));
    }
}
