package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class WithdrawalDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private WithdrawalDao withdrawalDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private WithdrawalData withdrawalData;
    private WithdrawalEvent withdrawalEvent;

    @Before
    public void before() throws DaoException {
        withdrawalData = random(WithdrawalData.class);
        withdrawalDao.saveWithdrawalData(withdrawalData);
        withdrawalEvent = random(WithdrawalEvent.class);
        withdrawalEvent.setWithdrawalId(withdrawalData.getWithdrawalId());
        withdrawalDao.saveWithdrawalEvent(withdrawalEvent);
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.withdrawal_data, mst.withdrawal_event");
    }

    @Test
    public void testGetWithdrawalData() throws DaoException {
        WithdrawalData withdrawalDataGet = withdrawalDao.getWithdrawalData(this.withdrawalData.getWithdrawalId());
        assertEquals(withdrawalData.getCurrencyCode(), withdrawalDataGet.getCurrencyCode());
    }

    @Test
    public void testGetLastWithdrawalEvent() throws DaoException {
        WithdrawalEvent secondWithdrawalEvent = random(WithdrawalEvent.class);
        String withdrawalId = withdrawalData.getWithdrawalId();
        secondWithdrawalEvent.setWithdrawalId(withdrawalId);
        withdrawalDao.saveWithdrawalEvent(secondWithdrawalEvent);
        WithdrawalEvent lastWithdrawalEvent = withdrawalDao.getLastWithdrawalEvent(withdrawalId);
        assertEquals(lastWithdrawalEvent.getId().longValue(), Math.max(withdrawalEvent.getId(), secondWithdrawalEvent.getId()));
        assertEquals(withdrawalDao.getLastEventId().get().longValue(), Math.max(withdrawalEvent.getEventId(),secondWithdrawalEvent.getEventId() ));
    }

}
