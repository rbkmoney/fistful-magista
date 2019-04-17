package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatDeposit;
import com.rbkmoney.fistful.fistful_stat.StatRequest;
import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
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

public class DepositFunctionTest extends AbstractIntegrationTest {

    @Autowired
    private DepositDao depositDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DepositData deposit;
    private DepositData secondDeposit;

    @Before
    public void before() throws DaoException {
        super.before();
        deposit = random(DepositData.class);
        deposit.setId(1L);
        deposit.setEventCreatedAt(LocalDateTime.now().minusMinutes(1));
        secondDeposit = random(DepositData.class);
        secondDeposit.setId(2L);
        secondDeposit.setPartyId(deposit.getPartyId());
        secondDeposit.setIdentityId(deposit.getIdentityId());
        secondDeposit.setEventCreatedAt(LocalDateTime.now().minusMinutes(1));

        depositDao.save(deposit);
        depositDao.save(secondDeposit);
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.deposit");
    }

    @Test
    public void testOneDeposit() throws DaoException {
        String json = String.format(
                "{'query': {'deposits': {" +
                        "'deposit_id':'%s', " +
                        "'identity_id': '%s', " +
                        "'wallet_id':'%s', " +
                        "'source_id':'%s', " +
                        "'party_id': '%s', " +
                        "'amount_from':'%d', " +
                        "'amount_to':'%d', " +
                        "'currency_code':'%s', " +
                        "'status':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}}}",
                deposit.getDepositId(),
                deposit.getIdentityId(),
                deposit.getWalletId(),
                deposit.getSourceId(),
                deposit.getPartyId(),
                deposit.getAmount() - 1,
                deposit.getAmount() + 1,
                deposit.getCurrencyCode(),
                StringUtils.capitalize(deposit.getDepositStatus().getLiteral()),
                TypeUtil.temporalToString(deposit.getEventCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(deposit.getEventCreatedAt().plusHours(10))
        );
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        List<StatDeposit> deposits = statResponse.getData().getDeposits();
        assertEquals(1, deposits.size());
    }

    @Test
    public void testAllWallets() throws DaoException {
        String json = String.format(
                "{'query': {'deposits': {'party_id': '%s','identity_id': '%s'}}}",
                deposit.getPartyId(),
                deposit.getIdentityId()
        );
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        List<StatDeposit> deposits = statResponse.getData().getDeposits();
        assertEquals(2, deposits.size());
    }

    @Test(expected = QueryParserException.class)
    public void testWhenSizeOverflow() {
        String json = "{'query': {'deposits': {'size': 1001}}}";
        queryProcessor.processQuery(new StatRequest(json));
    }

    @Test
    public void testContinuationToken() {
        String json = String.format(
                "{'query': {'deposits': {'party_id': '%s','identity_id': '%s'}, 'size':'1'}}",
                deposit.getPartyId(),
                deposit.getIdentityId()
        );
        StatRequest statRequest = new StatRequest(json);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getDeposits().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 2L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getDeposits().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 1L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertNull(statResponse.getContinuationToken());
    }

    @Test
    public void testIfNotPresentDeposits() {
        String json = "{'query': {'deposits': {'party_id': '6954b4d1-f39f-4cc1-8843-eae834e6f849','identity_id': 'csgo-better-than-1.6'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(0, statResponse.getData().getDeposits().size());
    }

    @Test(expected = BadTokenException.class)
    public void testBadToken() {
        String json = String.format(
                "{'query': {'deposits': {'party_id': '%s','identity_id': '%s'}, 'size':'1'}}",
                deposit.getPartyId(),
                deposit.getIdentityId()
        );
        StatRequest statRequest = new StatRequest(json);
        statRequest.setContinuationToken(UUID.randomUUID().toString());
        queryProcessor.processQuery(statRequest);
    }
}
