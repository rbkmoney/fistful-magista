package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
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

    @Before
    public void before() throws DaoException {
        withdrawalData = random(WithdrawalData.class);
        withdrawalDao.save(withdrawalData);
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.withdrawal_data");
    }

    @Test
    public void testGetWithdrawalData() throws DaoException {
        WithdrawalData withdrawalDataGet = withdrawalDao.get(this.withdrawalData.getWithdrawalId());
        assertEquals(withdrawalData.getCurrencyCode(), withdrawalDataGet.getCurrencyCode());
    }

}
