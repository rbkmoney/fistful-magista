package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatRequest;
import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.fistful.fistful_stat.StatWithdrawal;
import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.WithdrawalDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WithdrawalData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.dsl.BadTokenException;
import com.rbkmoney.magista.dsl.TokenUtil;
import com.rbkmoney.magista.dsl.parser.QueryParserException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class WithdrawalFunctionTest extends AbstractIntegrationTest {

    @Autowired
    private WithdrawalDao withdrawalDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private WithdrawalData withdrawalData;
    private WithdrawalData secondWithdrawalData;

    @Before
    public void before() throws DaoException {
        super.before();
        withdrawalData = random(WithdrawalData.class);
        withdrawalData.setId(1L);
        withdrawalData.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        withdrawalData.setWithdrawalId(withdrawalData.getWithdrawalId());
        withdrawalDao.save(withdrawalData);
        secondWithdrawalData = random(WithdrawalData.class);
        secondWithdrawalData.setId(2L);
        secondWithdrawalData.setPartyId(withdrawalData.getPartyId());
        secondWithdrawalData.setIdentityId(withdrawalData.getIdentityId());
        secondWithdrawalData.setCreatedAt(LocalDateTime.now());
        withdrawalDao.save(secondWithdrawalData);
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.withdrawal_data");
    }

    @Test
    public void testOneWithdrawal() throws DaoException {
        String json = String.format("{'query': {'withdrawals': {" +
                        "'party_id': '%s', " +
                        "'wallet_id':'%s', " +
                        "'withdrawal_id':'%s', " +
                        "'identity_id': '%s', " +
                        "'destination_id':'%s', " +
                        "'status':'%s', " +
                        "'currency_code':'%s', " +
                        "'amount_from':'%d', " +
                        "'amount_to':'%d', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}}}",
                withdrawalData.getPartyId(),
                withdrawalData.getWalletId(),
                withdrawalData.getWithdrawalId(),
                withdrawalData.getIdentityId(),
                withdrawalData.getDestinationId(),
                StringUtils.capitalize(withdrawalData.getWithdrawalStatus().getLiteral()),
                withdrawalData.getCurrencyCode(),
                withdrawalData.getAmount() - 1,
                withdrawalData.getAmount() + 1,
                TypeUtil.temporalToString(withdrawalData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(withdrawalData.getCreatedAt().plusHours(10))
        );
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        List<StatWithdrawal> withdrawals = statResponse.getData().getWithdrawals();
        assertEquals(1, withdrawals.size());
    }

    @Test
    public void testAllWallets() throws DaoException {
        String json = String.format("{'query': {'withdrawals': {'party_id': '%s','identity_id': '%s'}}}",
                withdrawalData.getPartyId(),
                withdrawalData.getIdentityId());
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        List<StatWithdrawal> withdrawals = statResponse.getData().getWithdrawals();
        assertEquals(2, withdrawals.size());
    }

    @Test(expected = QueryParserException.class)
    public void testWhenSizeOverflow() {
        String json = "{'query': {'withdrawals': {'size': 1001}}}";
        queryProcessor.processQuery(new StatRequest(json));
    }

    @Test
    public void testContinuationToken() {
        String json = String.format("{'query': {'withdrawals': {'party_id': '%s','identity_id': '%s'}, 'size':'1'}}",
                withdrawalData.getPartyId(),
                withdrawalData.getIdentityId());
        StatRequest statRequest = new StatRequest(json);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getWithdrawals().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 2L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getWithdrawals().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 1L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertNull(statResponse.getContinuationToken());
    }

    @Test
    public void testIfNotPresentWithdrawals() {
        String json = "{'query': {'withdrawals': {'party_id': '6954b4d1-f39f-4cc1-8843-eae834e6f849'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(0, statResponse.getData().getWithdrawals().size());
    }

    @Test(expected = BadTokenException.class)
    public void testBadToken() {
        String json = String.format("{'query': {'withdrawals': {'party_id': '%s','identity_id': '%s'}, 'size':'1'}}",
                withdrawalData.getPartyId(),
                withdrawalData.getIdentityId());
        StatRequest statRequest = new StatRequest(json);
        statRequest.setContinuationToken(UUID.randomUUID().toString());
        queryProcessor.processQuery(statRequest);
    }


}
