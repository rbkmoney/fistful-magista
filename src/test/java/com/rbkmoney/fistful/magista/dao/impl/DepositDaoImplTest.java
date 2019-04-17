package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DepositDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private DepositDao depositDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void depositDaoTest() throws DaoException {
        DepositData deposit = random(DepositData.class);
        deposit.setId(null);
        depositDao.save(deposit);
        Long id = depositDao.save(deposit);
        deposit.setId(id);
        assertEquals(deposit, depositDao.get(deposit.getDepositId()));
        assertNull(depositDao.get(deposit.getDepositId()));
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.deposit_data");
    }
}
