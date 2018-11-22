package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.fistful_stat.StatWallet;
import com.rbkmoney.fistful.fistful_stat.StatWithdrawal;
import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.SearchDao;
import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletEvent;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.query.impl.WalletFunction;
import com.rbkmoney.fistful.magista.query.impl.WithdrawalFunction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.rbkmoney.fistful.magista.query.impl.Parameters.*;
import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class SearchDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private SearchDao searchDao;
    @Autowired
    private WalletDao walletDao;
    @Autowired
    private WithdrawalDao withdrawalDao;

    @Test
    public void testGetWallets() throws DaoException {
        WalletData walletData = random(WalletData.class);
        walletDao.saveWalletData(walletData);
        WalletEvent walletEvent = random(WalletEvent.class);
        walletEvent.setWalletId(walletData.getWalletId());
        walletEvent.setIdentityId(walletData.getIdentityId());
        walletDao.saveWalletEvent(walletEvent);
        HashMap<String, Object> map = new HashMap<>();
        map.put(PARTY_ID_PARAM, walletData.getPartyId());
        map.put(IDENTITY_ID_PARAM, walletData.getIdentityId());
        map.put(CURRENCY_CODE_PARAM, walletEvent.getCurrencyCode());
        WalletFunction.WalletParameters walletParameters = new WalletFunction.WalletParameters(map, null);
        Collection<Map.Entry<Long, StatWallet>> wallets = searchDao.getWallets(walletParameters, Optional.of(walletData.getId() + 1), 100);
        assertEquals(wallets.size(), 1);
        assertEquals(wallets.iterator().next().getValue().getName(), walletData.getWalletName());

        map.clear();
        map.put(PARTY_ID_PARAM, UUID.randomUUID());
        walletParameters = new WalletFunction.WalletParameters(map, null);
        wallets = searchDao.getWallets(walletParameters, Optional.of(walletData.getId() + 1), 100);
        assertEquals(wallets.size(), 0);
    }

    @Test
    public void testGetWithdrawals() throws DaoException {
        WithdrawalData withdrawalData = random(WithdrawalData.class);
        withdrawalDao.saveWithdrawalData(withdrawalData);
        WithdrawalEvent withdrawalEvent = random(WithdrawalEvent.class);
        withdrawalEvent.setWithdrawalId(withdrawalData.getWithdrawalId());
        withdrawalDao.saveWithdrawalEvent(withdrawalEvent);
        HashMap<String, Object> map = new HashMap<>();
        map.put(PARTY_ID_PARAM, withdrawalData.getPartyId());
        map.put(WALLET_ID_PARAM, withdrawalData.getWalletId());
        map.put(IDENTITY_ID_PARAM, withdrawalData.getIdentityId());
        map.put(WITHDRAWAL_DESTINATION_ID_PARAM, withdrawalData.getDestinationId());
        map.put(WITHDRAWAL_STATUS_PARAM, withdrawalEvent.getWithdrawalStatus().getLiteral());
        map.put(WITHDRAWAL_AMOUNT_FROM_PARAM, withdrawalData.getAmount() - 1);
        map.put(WITHDRAWAL_AMOUNT_TO_PARAM, withdrawalData.getAmount() + 1);
        map.put(CURRENCY_CODE_PARAM, withdrawalData.getCurrencyCode());
        WithdrawalFunction.WithdrawalParameters withdrawalParameters = new WithdrawalFunction.WithdrawalParameters(map, null);
        Collection<Map.Entry<Long, StatWithdrawal>> withdrawals = searchDao.getWithdrawals(withdrawalParameters, Optional.of(withdrawalEvent.getEventCreatedAt().minusMinutes(1)), Optional.of(withdrawalEvent.getEventCreatedAt().plusMinutes(1)), Optional.of(withdrawalData.getId() + 1), 100);
        assertEquals(withdrawals.size(), 1);
        assertEquals(withdrawals.iterator().next().getValue().getFee(), withdrawalEvent.getFee().longValue());

        map.clear();
        map.put(IDENTITY_ID_PARAM, "wrong_identity_id");
        withdrawalParameters = new WithdrawalFunction.WithdrawalParameters(map, null);
        withdrawals = searchDao.getWithdrawals(withdrawalParameters, Optional.of(withdrawalEvent.getEventCreatedAt().minusMinutes(1)), Optional.of(withdrawalEvent.getEventCreatedAt().plusMinutes(1)), Optional.of(withdrawalData.getId() + 1), 100);
        assertEquals(withdrawals.size(), 0);
    }
}
