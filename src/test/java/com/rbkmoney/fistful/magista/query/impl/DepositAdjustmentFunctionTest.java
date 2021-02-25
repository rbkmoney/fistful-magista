package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatDepositAdjustment;
import com.rbkmoney.fistful.fistful_stat.StatRequest;
import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.DepositAdjustmentDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositAdjustmentData;
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

public class DepositAdjustmentFunctionTest extends AbstractIntegrationTest {

    @Autowired
    private DepositAdjustmentDao depositAdjustmentDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DepositAdjustmentData depositAdjustmentData;
    private DepositAdjustmentData secondDepositAdjustmentData;

    @Before
    public void before() throws DaoException {
        super.before();
        depositAdjustmentData = random(DepositAdjustmentData.class);
        depositAdjustmentData.setId(1L);
        depositAdjustmentData.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        secondDepositAdjustmentData = random(DepositAdjustmentData.class);
        secondDepositAdjustmentData.setId(2L);
        secondDepositAdjustmentData.setPartyId(depositAdjustmentData.getPartyId());
        secondDepositAdjustmentData.setIdentityId(depositAdjustmentData.getIdentityId());
        secondDepositAdjustmentData.setCreatedAt(LocalDateTime.now().minusMinutes(1));

        depositAdjustmentDao.save(depositAdjustmentData);
        depositAdjustmentDao.save(secondDepositAdjustmentData);
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.deposit_adjustment_data");
    }

    @Test
    public void testOneDepositAdjustment() throws DaoException {
        String json = String.format(
                "{'query': {'deposits_adjustments': {" +
                        "'party_id': '%s', " +
                        "'identity_id': '%s', " +
                        "'source_id':'%s', " +
                        "'wallet_id':'%s', " +
                        "'deposit_id':'%s', " +
                        "'adjustment_id':'%s', " +
                        "'amount_from':'%d', " +
                        "'amount_to':'%d', " +
                        "'currency_code':'%s', " +
                        "'status':'%s', " +
                        "'deposit_status':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}}}",
                depositAdjustmentData.getPartyId(),
                depositAdjustmentData.getIdentityId(),
                depositAdjustmentData.getSourceId(),
                depositAdjustmentData.getWalletId(),
                depositAdjustmentData.getDepositId(),
                depositAdjustmentData.getAdjustmentId(),
                depositAdjustmentData.getAmount() - 1,
                depositAdjustmentData.getAmount() + 1,
                depositAdjustmentData.getCurrencyCode(),
                StringUtils.capitalize(depositAdjustmentData.getStatus().getLiteral()),
                StringUtils.capitalize(depositAdjustmentData.getDepositStatus().getLiteral()),
                TypeUtil.temporalToString(depositAdjustmentData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(depositAdjustmentData.getCreatedAt().plusHours(10))
        );
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        List<StatDepositAdjustment> depositAdjustments = statResponse.getData().getDepositAdjustments();
        assertEquals(1, depositAdjustments.size());
    }

    @Test
    public void testAllDepositsAdjustments() throws DaoException {
        String json = String.format(
                "{'query': {'deposits_adjustments': {'party_id': '%s','identity_id': '%s'}}}",
                depositAdjustmentData.getPartyId(),
                depositAdjustmentData.getIdentityId()
        );
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        List<StatDepositAdjustment> depositAdjustments = statResponse.getData().getDepositAdjustments();
        assertEquals(2, depositAdjustments.size());
    }

    @Test(expected = QueryParserException.class)
    public void testWhenSizeOverflow() {
        String json = "{'query': {'deposits_adjustments': {'size': 1001}}}";
        queryProcessor.processQuery(new StatRequest(json));
    }

    @Test
    public void testContinuationToken() {
        String json = String.format(
                "{'query': {'deposits_adjustments': {'party_id': '%s','identity_id': '%s'}, 'size':'1'}}",
                depositAdjustmentData.getPartyId(),
                depositAdjustmentData.getIdentityId()
        );
        StatRequest statRequest = new StatRequest(json);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getDepositAdjustments().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 2L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getDepositAdjustments().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 1L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertNull(statResponse.getContinuationToken());
    }

    @Test
    public void testIfNotPresentDepositsAdjustments() {
        String json = "{'query': {'deposits_adjustments': {'party_id': '6954b4d1-f39f-4cc1-8843-eae834e6f849','identity_id': 'nuda'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(0, statResponse.getData().getDepositAdjustments().size());
    }

    @Test(expected = BadTokenException.class)
    public void testBadToken() {
        String json = String.format(
                "{'query': {'deposits_adjustments': {'party_id': '%s','identity_id': '%s'}, 'size':'1'}}",
                depositAdjustmentData.getPartyId(),
                depositAdjustmentData.getIdentityId()
        );
        StatRequest statRequest = new StatRequest(json);
        statRequest.setContinuationToken(UUID.randomUUID().toString());
        queryProcessor.processQuery(statRequest);
    }

    @Test
    public void testWithoutParameters() {
        String dsl = "{'query': {'deposits_adjustments': {}, 'size':'1'}}";
        StatRequest statRequest = new StatRequest(dsl);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getDepositAdjustments().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenPartyIdIncorrect() {
        String dsl = "{'query': {'deposits_adjustments': {'party_id': 'qwe'}}}";
        StatRequest statRequest = new StatRequest(dsl);
        queryProcessor.processQuery(statRequest);
    }
}
