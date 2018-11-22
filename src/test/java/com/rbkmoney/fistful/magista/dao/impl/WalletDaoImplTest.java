package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletEvent;
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

public class WalletDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private WalletData walletData;
    private WalletEvent walletEvent;

    @Before
    public void before() throws DaoException {
        walletData = random(WalletData.class);
        walletDao.saveWalletData(walletData);
        walletEvent = random(WalletEvent.class);
        walletEvent.setWalletId(walletData.getWalletId());
        walletDao.saveWalletEvent(walletEvent);
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.wallet_data, mst.wallet_event");
    }

    @Test
    public void testGetWalletData() throws DaoException {
        WalletData walletDataGet = walletDao.getWalletData(this.walletData.getWalletId());
        assertEquals(walletData.getWalletName(), walletDataGet.getWalletName());
    }

    @Test
    public void testGetLastWalletEvent() throws DaoException {
        WalletEvent secondWalletEvent = random(WalletEvent.class);
        String walletId = walletData.getWalletId();
        secondWalletEvent.setWalletId(walletId);
        walletDao.saveWalletEvent(secondWalletEvent);
        WalletEvent lastWalletEvent = walletDao.getLastWalletEvent(walletId);
        assertEquals(lastWalletEvent.getId().longValue(), Math.max(walletEvent.getId(), secondWalletEvent.getId()));
        assertEquals(walletDao.getLastEventId().get().longValue(), Math.max(walletEvent.getEventId(), secondWalletEvent.getEventId()));
    }

    @Test
    public void testCorrectUpdateWalletData() throws DaoException {
        WalletData walletDataGet = walletDao.getWalletData(this.walletData.getWalletId());
        String modifiedWalletName = "kektus";
        walletDataGet.setWalletName(modifiedWalletName);
        walletDao.saveWalletData(walletDataGet);
        assertEquals(walletDao.getWalletData(this.walletData.getWalletId()).getWalletName(), modifiedWalletName);
    }
}
