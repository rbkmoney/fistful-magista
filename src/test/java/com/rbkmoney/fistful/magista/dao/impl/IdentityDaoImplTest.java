package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class IdentityDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private IdentityDao identityDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private IdentityData identityData;
    private IdentityEvent identityEvent;

    @Before
    public void before() throws DaoException {
        identityData = random(IdentityData.class);
        identityDao.saveIdentityData(identityData);
        identityEvent = random(IdentityEvent.class);
        identityEvent.setIdentityId(identityData.getIdentityId());
        identityDao.saveIdentityEvent(identityEvent);
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.identity_data, mst.identity_event");
    }

    @Test
    public void testGetIdentityData() throws DaoException {
        IdentityData challengeDataGet = identityDao.getIdentityData(this.identityData.getIdentityId());
        assertEquals(identityData.getIdentityProviderId(), challengeDataGet.getIdentityProviderId());
    }

    @Test
    public void testGetLastIdentityEvent() throws DaoException {
        IdentityEvent secondIdentityEvent = random(IdentityEvent.class);
        String identityId = identityData.getIdentityId();
        secondIdentityEvent.setIdentityId(identityId);
        identityDao.saveIdentityEvent(secondIdentityEvent);
        IdentityEvent lastIdentityEvent = identityDao.getLastIdentityEvent(identityId);
        assertEquals(lastIdentityEvent.getId().longValue(), Math.max(identityEvent.getId(), secondIdentityEvent.getId()));
        assertEquals(identityDao.getLastEventId().get().longValue(), Math.max(identityEvent.getEventId(),secondIdentityEvent.getEventId() ));
    }

}
