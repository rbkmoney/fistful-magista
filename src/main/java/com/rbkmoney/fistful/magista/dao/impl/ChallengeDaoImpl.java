package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.ChallengeDao;
import com.rbkmoney.fistful.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeEvent;
import com.rbkmoney.fistful.magista.domain.tables.records.ChallengeDataRecord;
import com.rbkmoney.fistful.magista.domain.tables.records.ChallengeEventRecord;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.fistful.magista.domain.tables.ChallengeData.CHALLENGE_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.ChallengeEvent.CHALLENGE_EVENT;

@Component
public class ChallengeDaoImpl extends AbstractGenericDao implements ChallengeDao {

    private final RecordRowMapper<ChallengeData> challengeDataRecordRowMapper;
    private final RecordRowMapper<ChallengeEvent> challengeEventRecordRowMapper;

    @Autowired
    public ChallengeDaoImpl(DataSource dataSource) {
        super(dataSource);
        challengeDataRecordRowMapper = new RecordRowMapper<>(CHALLENGE_DATA, ChallengeData.class);
        challengeEventRecordRowMapper = new RecordRowMapper<>(CHALLENGE_EVENT, ChallengeEvent.class);
    }

    @Override
    public ChallengeData getChallengeData(String identityId, String challengeId) throws DaoException {
        Query query = getDslContext().selectFrom(CHALLENGE_DATA)
                .where(
                        CHALLENGE_DATA.IDENTITY_ID.eq(identityId)
                                .and(CHALLENGE_DATA.CHALLENGE_ID.eq(challengeId))
                );

        return fetchOne(query, challengeDataRecordRowMapper);
    }

    @Override
    public long saveChallengeData(ChallengeData challengeData) throws DaoException {
        ChallengeDataRecord challengeDataRecord = getDslContext().newRecord(CHALLENGE_DATA, challengeData);

        Query query = getDslContext().insertInto(CHALLENGE_DATA)
                .set(challengeDataRecord)
                .onConflict(CHALLENGE_DATA.IDENTITY_ID, CHALLENGE_DATA.CHALLENGE_ID)
                .doNothing()
                .returning(CHALLENGE_DATA.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public ChallengeEvent getLastChallengeEvent(String identityId, String challengeId) throws DaoException {
        Query query = getDslContext()
                .selectFrom(CHALLENGE_EVENT)
                .where(
                        CHALLENGE_EVENT.IDENTITY_ID.eq(identityId)
                                .and(CHALLENGE_EVENT.CHALLENGE_ID.eq(challengeId))
                )
                .orderBy(CHALLENGE_EVENT.ID.desc())
                .limit(1);

        return fetchOne(query, challengeEventRecordRowMapper);
    }

    @Override
    public long saveChallengeEvent(ChallengeEvent challengeEvent) throws DaoException {
        ChallengeEventRecord challengeEventRecord = getDslContext().newRecord(CHALLENGE_EVENT, challengeEvent);

        Query query = getDslContext().insertInto(CHALLENGE_EVENT)
                .set(challengeEventRecord)
                .onConflict(CHALLENGE_EVENT.EVENT_ID, CHALLENGE_EVENT.SEQUENCE_ID)
                .doUpdate()
                .set(challengeEventRecord)
                .returning(CHALLENGE_EVENT.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }
}
