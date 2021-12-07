package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatIdentity;
import com.rbkmoney.fistful.fistful_stat.StatRequest;
import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.fistful.magista.AbstractIntegrationTest;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class IdentityFunctionTest extends AbstractIntegrationTest {

    @Autowired
    private IdentityDao identityDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private IdentityData identityData;
    private IdentityData identityDataSecond;
    private ChallengeData challengeData;
    private ChallengeData challengeDataSecond;
    private ChallengeData challengeDataThird;

    @Before
    public void before() throws DaoException {
        super.before();
        identityData = random(IdentityData.class);
        identityData.setId(1L);
        identityData.setIdentityId("1");
        identityData.setIdentityProviderId("test");
        identityData.setCreatedAt(LocalDateTime.now().minusMinutes(1));

        identityDataSecond = random(IdentityData.class);
        identityDataSecond.setId(2L);
        identityDataSecond.setIdentityId("2");
        identityDataSecond.setIdentityProviderId(identityData.getIdentityProviderId());
        identityDataSecond.setPartyId(identityData.getPartyId());
        identityDataSecond.setPartyContractId(identityData.getPartyContractId());
        identityDataSecond.setCreatedAt(LocalDateTime.now().minusMinutes(1));

        identityDao.save(identityData);
        identityDao.save(identityDataSecond);

        challengeData = random(ChallengeData.class);
        challengeData.setId(1L);
        challengeData.setChallengeId("1");
        challengeData.setIdentityId(identityData.getIdentityId());
        challengeData.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        challengeData.setChallengeStatus(ChallengeStatus.pending);
        challengeData.setChallengeValidUntil(LocalDateTime.now().minusMinutes(1));

        challengeDataSecond = random(ChallengeData.class);
        challengeDataSecond.setId(2L);
        challengeDataSecond.setChallengeId("2");
        challengeDataSecond.setIdentityId(challengeData.getIdentityId());
        challengeDataSecond.setChallengeClassId(challengeData.getChallengeClassId());
        challengeDataSecond.setChallengeStatus(ChallengeStatus.completed);
        challengeDataSecond.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        challengeDataSecond.setChallengeValidUntil(LocalDateTime.now().minusMinutes(1));

        challengeDataThird = random(ChallengeData.class);
        challengeDataThird.setId(3L);
        challengeDataThird.setChallengeId("3");
        challengeDataThird.setIdentityId(identityDataSecond.getIdentityId());
        challengeDataThird.setChallengeClassId(challengeData.getChallengeClassId());
        challengeDataThird.setChallengeStatus(ChallengeStatus.completed);
        challengeDataThird.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        challengeDataThird.setChallengeValidUntil(LocalDateTime.now().minusMinutes(1));

        identityDao.save(challengeData);
        identityDao.save(challengeDataSecond);
        identityDao.save(challengeDataThird);
    }

    @After
    public void after() {
        jdbcTemplate.execute("truncate mst.identity_data; truncate mst.challenge_data");
    }

    @Test
    public void testOneIdentity() throws DaoException {
        String json = String.format(
                "{'query': {'identities': {" +
                        "'party_id':'%s', " +
                        "'party_contract_id': '%s', " +
                        "'identity_id':'%s', " +
                        "'identity_provider_id':'%s', " +
                        "'identity_effective_challenge_id':'%s', " +
                        "'identity_level_id':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}}}",
                identityData.getPartyId(),
                identityData.getPartyContractId(),
                identityData.getIdentityId(),
                identityData.getIdentityProviderId(),
                identityData.getIdentityEffectiveChallengeId(),
                identityData.getIdentityLevelId(),
                TypeUtil.temporalToString(identityData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(identityData.getCreatedAt().plusHours(10))
        );
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        List<StatIdentity> identities = statResponse.getData().getIdentities();
        assertEquals(2, identities.size());

        json = String.format(
                "{'query': {'identities': {" +
                        "'identity_id':'%s' " +
                        "}}}",
                identityData.getIdentityId()
        );
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        identities = statResponse.getData().getIdentities();
        assertEquals(2, identities.size());

        json = String.format(
                "{'query': {'identities': {" +
                        "'party_id':'%s', " +
                        "'party_contract_id': '%s', " +
                        "'identity_id':'%s', " +
                        "'identity_provider_id':'%s', " +
                        "'identity_effective_challenge_id':'%s', " +
                        "'identity_level_id':'%s', " +
                        "'challenge_id':'%s', " +
                        "'challenge_class_id':'%s', " +
                        "'challenge_status':'%s', " +
                        "'challenge_resolution':'%s', " +
                        "'challenge_valid_until':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}}}",
                identityData.getPartyId(),
                identityData.getPartyContractId(),
                identityData.getIdentityId(),
                identityData.getIdentityProviderId(),
                identityData.getIdentityEffectiveChallengeId(),
                identityData.getIdentityLevelId(),
                challengeData.getChallengeId(),
                challengeData.getChallengeClassId(),
                "Pending",
                challengeData.getChallengeResolution(),
                TypeUtil.temporalToString(challengeData.getChallengeValidUntil()),
                TypeUtil.temporalToString(identityData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(identityData.getCreatedAt().plusHours(10))
        );
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        identities = statResponse.getData().getIdentities();
        assertEquals(1, identities.size());

        json = String.format(
                "{'query': {'identities': {" +
                        "'identity_id':'%s', " +
                        "'challenge_id':'%s' " +
                        "}}}",
                identityData.getIdentityId(),
                challengeData.getChallengeId()
        );
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        identities = statResponse.getData().getIdentities();
        assertEquals(1, identities.size());
    }

    @Test
    public void testAllIdentities() throws DaoException {
        String json = String.format(
                "{'query': {'identities': {" +
                        "'party_id':'%s', " +
                        "'party_contract_id': '%s', " +
                        "'identity_provider_id':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}}}",
                identityData.getPartyId(),
                identityData.getPartyContractId(),
                identityData.getIdentityProviderId(),
                TypeUtil.temporalToString(identityData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(identityData.getCreatedAt().plusHours(10))
        );
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        List<StatIdentity> identities = statResponse.getData().getIdentities();
        assertEquals(3, identities.size());

        json = String.format(
                "{'query': {'identities': {" +
                        "'party_id':'%s', " +
                        "'party_contract_id': '%s', " +
                        "'identity_provider_id':'%s', " +
                        "'challenge_class_id':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}}}",
                identityData.getPartyId(),
                identityData.getPartyContractId(),
                identityData.getIdentityProviderId(),
                challengeData.getChallengeClassId(),
                TypeUtil.temporalToString(identityData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(identityData.getCreatedAt().plusHours(10))
        );
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        identities = statResponse.getData().getIdentities();
        assertEquals(3, identities.size());

        json = String.format(
                "{'query': {'identities': {" +
                        "'party_id':'%s', " +
                        "'party_contract_id': '%s', " +
                        "'identity_provider_id':'%s', " +
                        "'challenge_class_id':'%s', " +
                        "'challenge_status':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}}}",
                identityData.getPartyId(),
                identityData.getPartyContractId(),
                identityData.getIdentityProviderId(),
                challengeData.getChallengeClassId(),
                "Completed",
                TypeUtil.temporalToString(identityData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(identityData.getCreatedAt().plusHours(10))
        );
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        identities = statResponse.getData().getIdentities();
        assertEquals(2, identities.size());

        json = String.format(
                "{'query': {'identities': {" +
                        "'party_id':'%s', " +
                        "'party_contract_id': '%s', " +
                        "'identity_provider_id':'%s', " +
                        "'challenge_class_id':'%s', " +
                        "'challenge_id':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}}}",
                identityData.getPartyId(),
                identityData.getPartyContractId(),
                identityData.getIdentityProviderId(),
                challengeData.getChallengeClassId(),
                challengeData.getChallengeId(),
                TypeUtil.temporalToString(identityData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(identityData.getCreatedAt().plusHours(10))
        );
        statResponse = queryProcessor.processQuery(new StatRequest(json));
        identities = statResponse.getData().getIdentities();
        assertEquals(1, identities.size());
    }

    @Test(expected = QueryParserException.class)
    public void testWhenSizeOverflow() {
        String json = "{'query': {'identities': {'size': 1001}}}";
        queryProcessor.processQuery(new StatRequest(json));
    }

    @Test
    public void testContinuationToken() {
        String json = String.format(
                "{'query': {'identities': {" +
                        "'party_id':'%s', " +
                        "'party_contract_id': '%s', " +
                        "'identity_provider_id':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}, 'size':'1'}}",
                identityData.getPartyId(),
                identityData.getPartyContractId(),
                identityData.getIdentityProviderId(),
                TypeUtil.temporalToString(identityData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(identityData.getCreatedAt().plusHours(10))
        );
        StatRequest statRequest = new StatRequest(json);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getIdentities().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 2L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(1, statResponse.getData().getIdentities().size());
        assertNotNull(statResponse.getContinuationToken());
        assertEquals((Long) 1L, TokenUtil.extractIdValue(statResponse.getContinuationToken()).get());

        statRequest.setContinuationToken(statResponse.getContinuationToken());
        statResponse = queryProcessor.processQuery(statRequest);
        assertNull(statResponse.getContinuationToken());
    }

    @Test
    public void testIfNotPresent() {
        String json = "{'query': {'identities': {'party_id': " +
                "'6954b4d1-f39f-4cc1-8843-eae834e6f849','identity_id': 'kekislav'}}}";
        StatResponse statResponse = queryProcessor.processQuery(new StatRequest(json));
        assertEquals(0, statResponse.getData().getIdentities().size());
    }

    @Test(expected = BadTokenException.class)
    public void testBadToken() {
        String json = String.format(
                "{'query': {'identities': {" +
                        "'party_id':'%s', " +
                        "'party_contract_id': '%s', " +
                        "'identity_provider_id':'%s', " +
                        "'from_time': '%s'," +
                        "'to_time': '%s'" +
                        "}, 'size':'1'}}",
                identityData.getPartyId(),
                identityData.getPartyContractId(),
                identityData.getIdentityProviderId(),
                TypeUtil.temporalToString(identityData.getCreatedAt().minusHours(10)),
                TypeUtil.temporalToString(identityData.getCreatedAt().plusHours(10))
        );
        StatRequest statRequest = new StatRequest(json);
        statRequest.setContinuationToken(UUID.randomUUID().toString());
        queryProcessor.processQuery(statRequest);
    }

    @Test
    public void testWithoutParameters() {
        String expectedSize = "1";
        String dsl = "{'query': {'identities': {}, 'size':'" + expectedSize + "'}}";
        StatRequest statRequest = new StatRequest(dsl);
        StatResponse statResponse = queryProcessor.processQuery(statRequest);
        assertEquals(expectedSize, String.valueOf(statResponse.getData().getIdentities().size()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenPartyIdIncorrect() {
        String dsl = "{'query': {'identities': {'party_id': 'qwe'}}}";
        StatRequest statRequest = new StatRequest(dsl);
        queryProcessor.processQuery(statRequest);
    }
}
