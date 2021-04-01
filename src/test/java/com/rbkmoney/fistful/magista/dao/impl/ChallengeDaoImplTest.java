package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class ChallengeDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private IdentityDao identityDao;

    private ChallengeData challengeData;

    @Before
    public void before() throws DaoException {
        challengeData = random(ChallengeData.class);
        identityDao.save(challengeData);
    }


    @Test
    public void testGetChallengeData() throws DaoException {
        ChallengeData challengeDataGet = identityDao.get(
                this.challengeData.getIdentityId(),
                this.challengeData.getChallengeId());
        assertEquals(challengeData.getChallengeClassId(), challengeDataGet.getChallengeClassId());
    }

}
