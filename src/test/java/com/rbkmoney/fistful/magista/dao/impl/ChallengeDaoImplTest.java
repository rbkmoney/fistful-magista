package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.ChallengeDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class ChallengeDaoImplTest extends AbstractIntegrationTest{

    @Autowired
    private ChallengeDao challengeDao;

    private ChallengeData challengeData;
    private ChallengeEvent challengeEvent;

    @Before
    public void before() throws DaoException {
        challengeData = random(ChallengeData.class);
        challengeDao.saveChallengeData(challengeData);
        challengeEvent = random(ChallengeEvent.class);
        challengeEvent.setIdentityId(challengeData.getIdentityId());
        challengeEvent.setChallengeId(challengeData.getChallengeId());
        challengeDao.saveChallengeEvent(challengeEvent);
    }


    @Test
    public void testGetChallengeData() throws DaoException {
        ChallengeData challengeDataGet = challengeDao.getChallengeData(this.challengeData.getIdentityId(), this.challengeData.getChallengeId());
        assertEquals(challengeData.getChallengeClassId(), challengeDataGet.getChallengeClassId());
    }

    @Test
    public void testGetLastChallengeEvent() throws DaoException {
        ChallengeEvent secondChallengeEvent = random(ChallengeEvent.class);
        String identityId = challengeData.getIdentityId();
        secondChallengeEvent.setIdentityId(identityId);
        String challengeId = challengeData.getChallengeId();
        secondChallengeEvent.setChallengeId(challengeId);
        challengeDao.saveChallengeEvent(secondChallengeEvent);
        ChallengeEvent lastChallengeEvent = challengeDao.getLastChallengeEvent(identityId, challengeId);
        assertEquals(lastChallengeEvent.getId().longValue(), Math.max(challengeEvent.getId(), secondChallengeEvent.getId()));
    }
}
