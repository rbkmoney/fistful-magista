package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.DepositAdjustmentDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositAdjustmentDataEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositAdjustmentDataStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositAdjustmentData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class DepositAdjusmentDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private DepositAdjustmentDao depositAdjustmentDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void depositAdjustmentDaoTest() throws DaoException {
        DepositAdjustmentData deposit = random(DepositAdjustmentData.class);
        deposit.setId(null);
        deposit.setStatus(DepositAdjustmentDataStatus.pending);
        deposit.setEventType(DepositAdjustmentDataEventType.DEPOSIT_ADJUSTMENT_CREATED);

        depositAdjustmentDao.save(deposit);

        Long id = depositAdjustmentDao.save(deposit).get();
        deposit.setId(id);

        assertEquals(deposit, depositAdjustmentDao.get(deposit.getDepositId(), deposit.getAdjustmentId()));

        deposit.setId(null);
        deposit.setEventType(DepositAdjustmentDataEventType.DEPOSIT_ADJUSTMENT_STATUS_CHANGED);
        deposit.setStatus(DepositAdjustmentDataStatus.succeeded);

        id = depositAdjustmentDao.save(deposit).get();
        deposit.setId(id);

        assertEquals(deposit, depositAdjustmentDao.get(deposit.getDepositId(), deposit.getAdjustmentId()));
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.deposit_adjustment_data");
    }
}
