package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatDepositRevert;
import com.rbkmoney.fistful.fistful_stat.StatRequest;
import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.DepositRevertDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositRevertData;
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

public class DepositRevertFunctionTest extends AbstractIntegrationTest {

    @Autowired
    private DepositRevertDao depositRevertDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DepositRevertData depositRevertData;
    private DepositRevertData secondDepositRevertData;

    @Before
    public void before() throws DaoException {
        super.before();
        depositRevertData = random(DepositRevertData.class);
        depositRevertData.setId(1L);
        depositRevertData.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        secondDepositRevertData = random(DepositRevertData.class);
        secondDepositRevertData.setId(2L);
        secondDepositRevertData.setPartyId(depositRevertData.getPartyId());
        secondDepositRevertData.setIdentityId(depositRevertData.getIdentityId());
        secondDepositRevertData.setCreatedAt(LocalDateTime.now().minusMinutes(1));

        depositRevertDao.save(depositRevertData);
        depositRevertDao.save(secondDepositRevertData);
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.deposit_revert_data");
    }

    @Test
    public void testOneDepositRevert() throws DaoException {
        String json = String.format(
                "{'query': {'deposits_reverts': {" +
                        "'party_id': '%s', " +
                        "'identity_id': '%s', " +
                        "'source_id':'%s', " +
                        "'wallet_id':'%s', " +
                        "'deposit_id':'%s', " +
                        "'revert_id':'%s', " +
                        "'amount_from':'%d', " +
                        "'amount_to':'%d', " +
                        "'currency_code':'%s', " +
                        "'status':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}}}",
                depositRevertData.getPartyId(),
                depositRevertData.getIdentityId(),
                depositRevertData.getSourceId(),
                depositRevertData.getWalletId(),
                depositRevertData.getDepositId(),
                depositRevertData.getRevertId(),
                depositRevertData.getAmount() - 1,
                depositRevertData.getAmount() + 1,
                depositRevertData.getCurrencyCode(),
                StringUtils.capitalize(depositRevertData.getStatus().getLiteral()),
                TypeUtil.temporalToString(depositRevertData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(depositRevertData.getCreatedAt().plusHours(10))
        );
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        List<StatDepositRevert> depositReverts = statResponse.getData().getDepositReverts();
        assertEquals(1, depositReverts.size());
    }

    @Test
    public void testAllDepositsReverts() throws DaoException {
        String json = String.format(
                "{'query': {'deposits_reverts': {'party_id': '%s','identity_id': '%s'}}}",
                depositRevertData.getPartyId(),
                depositRevertData.getIdentityId()
        );
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        List<StatDepositRevert> depositReverts = statResponse.getData().getDepositReverts();
        assertEquals(2, depositReverts.size());
    }

    @Test(expected = QueryParserException.class)
    public void testWhenSizeOverflow() {
        String json = "{'query': {'deposits_reverts': {'size': 1001}}}";
        queryProcessor.processQuery(new StatRequest(json));
    }

    @Test
    public void testContinuationToken() {
        String json = String.format(
                "{'query': {'deposits_reverts': {'party_id': '%s','identity_id': '%s'}, 'size':'1'}}",
                depositRevertData.getPartyId(),
                depositRevertData.getIdentityId()
        );
        StatRequest statRequest = new StatRequest(json);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getDepositReverts().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 2L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getDepositReverts().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 1L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertNull(statResponse.getContinuationToken());
    }

    @Test
    public void testIfNotPresentDepositsReverts() {
        String json = "{'query': {'deposits_reverts': {'party_id': '6954b4d1-f39f-4cc1-8843-eae834e6f849','identity_id': 'nuda'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(0, statResponse.getData().getDepositReverts().size());
    }

    @Test(expected = BadTokenException.class)
    public void testBadToken() {
        String json = String.format(
                "{'query': {'deposits_reverts': {'party_id': '%s','identity_id': '%s'}, 'size':'1'}}",
                depositRevertData.getPartyId(),
                depositRevertData.getIdentityId()
        );
        StatRequest statRequest = new StatRequest(json);
        statRequest.setContinuationToken(UUID.randomUUID().toString());
        queryProcessor.processQuery(statRequest);
    }

    @Test
    public void testWithoutParameters() {
        String dsl = "{'query': {'deposits_reverts': {}, 'size':'1'}}";
        StatRequest statRequest = new StatRequest(dsl);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getDepositReverts().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenPartyIdIncorrect() {
        String dsl = "{'query': {'deposits_reverts': {'party_id': 'qwe'}}}";
        StatRequest statRequest = new StatRequest(dsl);
        queryProcessor.processQuery(statRequest);
    }
}
