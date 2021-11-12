package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.fistful_stat.RevertStatus;
import com.rbkmoney.fistful.fistful_stat.StatDeposit;
import com.rbkmoney.fistful.fistful_stat.StatWallet;
import com.rbkmoney.fistful.fistful_stat.StatWithdrawal;
import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.*;
import com.rbkmoney.fistful.magista.domain.enums.DepositRevertDataStatus;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositRevertData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.query.impl.WalletFunction;
import com.rbkmoney.fistful.magista.query.impl.WithdrawalFunction;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositParameters;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.rbkmoney.fistful.magista.query.impl.Parameters.*;
import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class SearchDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private SearchDao searchDao;

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private WithdrawalDao withdrawalDao;

    @Autowired
    private DepositDao depositDao;

    @Autowired
    private DepositRevertDao depositRevertDao;

    @Test
    public void testGetWallets() throws DaoException {
        WalletData walletData = random(WalletData.class);
        walletDao.save(walletData);
        HashMap<String, Object> map = new HashMap<>();
        map.put(PARTY_ID_PARAM, walletData.getPartyId());
        map.put(IDENTITY_ID_PARAM, walletData.getIdentityId());
        map.put(CURRENCY_CODE_PARAM, walletData.getCurrencyCode());
        WalletFunction.WalletParameters walletParameters = new WalletFunction.WalletParameters(map, null);
        Collection<Map.Entry<Long, StatWallet>> wallets = searchDao.getWallets(
                walletParameters,
                Optional.of(walletData.getId() + 1),
                100);
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
        withdrawalDao.save(withdrawalData);
        HashMap<String, Object> map = new HashMap<>();
        map.put(PARTY_ID_PARAM, withdrawalData.getPartyId());
        map.put(WALLET_ID_PARAM, withdrawalData.getWalletId());
        map.put(IDENTITY_ID_PARAM, withdrawalData.getIdentityId());
        map.put(DESTINATION_ID_PARAM, withdrawalData.getDestinationId());
        map.put(EXTERNAL_ID_PARAM, withdrawalData.getExternalId());
        map.put(STATUS_PARAM, withdrawalData.getWithdrawalStatus().getLiteral());
        map.put(AMOUNT_FROM_PARAM, withdrawalData.getAmount() - 1);
        map.put(AMOUNT_TO_PARAM, withdrawalData.getAmount() + 1);
        map.put(CURRENCY_CODE_PARAM, withdrawalData.getCurrencyCode());
        WithdrawalFunction.WithdrawalParameters withdrawalParameters =
                new WithdrawalFunction.WithdrawalParameters(map, null);
        Collection<Map.Entry<Long, StatWithdrawal>> withdrawals = searchDao.getWithdrawals(
                withdrawalParameters,
                withdrawalData.getCreatedAt().minusMinutes(1),
                withdrawalData.getCreatedAt().plusMinutes(1),
                withdrawalData.getId() + 1,
                100
        );
        assertEquals(withdrawals.size(), 1);
        assertEquals(withdrawals.iterator().next().getValue().getFee(), withdrawalData.getFee().longValue());

        map.clear();
        map.put(IDENTITY_ID_PARAM, "wrong_identity_id");
        withdrawalParameters = new WithdrawalFunction.WithdrawalParameters(map, null);
        withdrawals = searchDao.getWithdrawals(
                withdrawalParameters,
                withdrawalData.getEventCreatedAt().minusMinutes(1),
                withdrawalData.getEventCreatedAt().plusMinutes(1),
                withdrawalData.getId() + 1,
                100
        );
        assertEquals(withdrawals.size(), 0);
    }

    @Test
    public void testGetDeposits() throws DaoException {
        DepositData deposit = random(DepositData.class);
        depositDao.save(deposit);

        HashMap<String, Object> map = buildDepositSearchMap(deposit);

        Collection<Map.Entry<Long, StatDeposit>> deposits = getDeposits(deposit, new DepositParameters(map, null));
        assertEquals(1, deposits.size());
        StatDeposit statDeposit = deposits.iterator().next().getValue();
        assertEquals(statDeposit.getFee(), deposit.getFee().longValue());
        assertEquals(RevertStatus.none, statDeposit.getRevertStatus());

        map.clear();
        map.put(IDENTITY_ID_PARAM, "wrong_identity_id");
        map.put(PARTY_ID_PARAM, deposit.getPartyId());
        deposits = getDeposits(deposit, new DepositParameters(map, null));
        assertEquals(0, deposits.size());
    }

    @Test
    public void testRevertStatusFullDeposits() throws DaoException {
        DepositData depositOne = random(DepositData.class);
        depositDao.save(depositOne);

        DepositRevertData depositRevertDataOne = random(DepositRevertData.class);
        depositRevertDataOne.setStatus(DepositRevertDataStatus.succeeded);
        depositRevertDataOne.setPartyId(depositOne.getPartyId());
        depositRevertDataOne.setWalletId(depositOne.getWalletId());
        depositRevertDataOne.setDepositId(depositOne.getDepositId());
        depositRevertDataOne.setAmount(depositOne.getAmount());

        depositRevertDao.save(depositRevertDataOne);
        HashMap<String, Object> map = buildDepositSearchMap(depositOne);
        Collection<Map.Entry<Long, StatDeposit>> deposits = getDeposits(depositOne, new DepositParameters(map, null));
        assertEquals(1, deposits.size());
        assertEquals(RevertStatus.full, deposits.iterator().next().getValue().getRevertStatus());
    }

    @Test
    public void testRevertStatusNoneDeposits() throws DaoException {
        DepositData depositOne = random(DepositData.class);
        depositDao.save(depositOne);

        DepositRevertData depositRevertDataOne = random(DepositRevertData.class);
        depositRevertDataOne.setStatus(DepositRevertDataStatus.pending);
        depositRevertDataOne.setPartyId(depositOne.getPartyId());
        depositRevertDataOne.setWalletId(depositOne.getWalletId());
        depositRevertDataOne.setDepositId(depositOne.getDepositId());
        depositRevertDataOne.setAmount(depositOne.getAmount());

        depositRevertDao.save(depositRevertDataOne);
        HashMap<String, Object> map = buildDepositSearchMap(depositOne);
        Collection<Map.Entry<Long, StatDeposit>> deposits = getDeposits(depositOne, new DepositParameters(map, null));
        assertEquals(1, deposits.size());
        assertEquals(RevertStatus.none, deposits.iterator().next().getValue().getRevertStatus());
    }

    @Test
    public void testRevertStatusPartialDeposits() throws DaoException {
        DepositData depositOne = random(DepositData.class);
        depositOne.setAmount(100L);
        depositDao.save(depositOne);

        DepositRevertData depositRevertDataOne = random(DepositRevertData.class);
        depositRevertDataOne.setStatus(DepositRevertDataStatus.succeeded);
        depositRevertDataOne.setPartyId(depositOne.getPartyId());
        depositRevertDataOne.setWalletId(depositOne.getWalletId());
        depositRevertDataOne.setDepositId(depositOne.getDepositId());
        depositRevertDataOne.setAmount(50L);

        DepositRevertData depositRevertDataTwo = random(DepositRevertData.class);
        depositRevertDataTwo.setStatus(DepositRevertDataStatus.succeeded);
        depositRevertDataTwo.setPartyId(depositOne.getPartyId());
        depositRevertDataTwo.setWalletId(depositOne.getWalletId());
        depositRevertDataTwo.setDepositId(depositOne.getDepositId());
        depositRevertDataTwo.setAmount(40L);

        depositRevertDao.save(depositRevertDataOne);
        depositRevertDao.save(depositRevertDataTwo);

        HashMap<String, Object> map = buildDepositSearchMap(depositOne);
        Collection<Map.Entry<Long, StatDeposit>> deposits = getDeposits(depositOne, new DepositParameters(map, null));
        assertEquals(1, deposits.size());
        assertEquals(RevertStatus.partial, deposits.iterator().next().getValue().getRevertStatus());
    }

    private Collection<Map.Entry<Long, StatDeposit>> getDeposits(DepositData deposit, DepositParameters parameters)
            throws DaoException {
        return searchDao.getDeposits(
                parameters,
                null,
                null,
                deposit.getId() + 1,
                100
        );
    }

    private HashMap<String, Object> buildDepositSearchMap(DepositData deposit) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(IDENTITY_ID_PARAM, deposit.getIdentityId());
        map.put(WALLET_ID_PARAM, deposit.getWalletId());
        map.put(SOURCE_ID_PARAM, deposit.getSourceId());
        map.put(PARTY_ID_PARAM, deposit.getPartyId());
        map.put(AMOUNT_FROM_PARAM, deposit.getAmount() - 1);
        map.put(AMOUNT_TO_PARAM, deposit.getAmount() + 1);
        map.put(CURRENCY_CODE_PARAM, deposit.getCurrencyCode());
        map.put(STATUS_PARAM, StringUtils.capitalize(deposit.getDepositStatus().getLiteral()));
        return map;
    }
}
