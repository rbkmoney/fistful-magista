package com.rbkmoney.fistful.magista.kafka.listener;

import com.rbkmoney.fistful.identity.*;
import com.rbkmoney.fistful.magista.FistfulMagistaApplication;
import com.rbkmoney.fistful.magista.dao.IdentityDao;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Slf4j
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = FistfulMagistaApplication.class,
        properties = {"kafka.state.cache.size=0"})
public class IdentityEventListenerTest extends AbstractListenerTest {

    private static final long MESSAGE_TIMEOUT = 4_000L;

    @MockBean
    private IdentityDao identityDao;

    @Captor
    private ArgumentCaptor<IdentityData> identityCaptor;

    @Captor
    private ArgumentCaptor<ChallengeData> challengeCaptor;

    @Test
    public void shouldListenAndSaveIdentityCreated() throws InterruptedException, DaoException {
        // Given
        String expected = UUID.randomUUID().toString();
        TimestampedChange change = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.created(new Identity(expected, "provider", "cls")));

        SinkEvent sinkEvent = sinkEvent(machineEvent(new ThriftSerializer<>(), change));

        // When
        produce(sinkEvent, "mg-events-ff-identity");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(identityDao, times(1))
                .save(identityCaptor.capture());
        assertThat(identityCaptor.getValue().getPartyId().toString())
                .isEqualTo(expected);
    }

    @Test
    public void shouldListenAndSaveLevelChanged() throws InterruptedException, DaoException {
        // Given
        String expected = "upd";
        TimestampedChange change = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.level_changed(expected));

        SinkEvent sinkEvent = sinkEvent(machineEvent(new ThriftSerializer<>(), change));

        when(identityDao.get("source_id"))
                .thenReturn(new IdentityData());

        // When
        produce(sinkEvent, "mg-events-ff-identity");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(identityDao, times(1))
                .save(identityCaptor.capture());
        assertThat(identityCaptor.getValue().getIdentityLevelId())
                .isEqualTo(expected);
    }

    @Test
    public void shouldListenAndSaveEffectiveChallenge() throws InterruptedException, DaoException {
        // Given
        String expected = "id";
        TimestampedChange change = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.effective_challenge_changed(expected));

        SinkEvent sinkEvent = sinkEvent(machineEvent(new ThriftSerializer<>(), change));

        when(identityDao.get("source_id"))
                .thenReturn(new IdentityData());

        // When
        produce(sinkEvent, "mg-events-ff-identity");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(identityDao, times(1))
                .save(identityCaptor.capture());
        assertThat(identityCaptor.getValue().getIdentityEffectiveChallengeId())
                .isEqualTo(expected);
    }

    @Test
    public void shouldListenAndSaveChallengeCreated() throws InterruptedException, DaoException {
        // Given
        String expected = "cls";
        TimestampedChange change = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(getIdentityChallenge(expected));

        SinkEvent sinkEvent = sinkEvent(machineEvent(new ThriftSerializer<>(), change));

        // When
        produce(sinkEvent, "mg-events-ff-identity");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(identityDao, times(1))
                .save(challengeCaptor.capture());
        assertThat(challengeCaptor.getValue().getChallengeClassId())
                .isEqualTo(expected);
    }

    @Test
    public void shouldListenAndSaveChallengeStatus() throws InterruptedException, DaoException {
        // Given
        TimestampedChange change = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.identity_challenge(new ChallengeChange("id",getApprovedStatus())));

        SinkEvent sinkEvent = sinkEvent(machineEvent(new ThriftSerializer<>(), change));

        when(identityDao.get(anyString(), anyString()))
                .thenReturn(new ChallengeData());

        // When
        produce(sinkEvent, "mg-events-ff-identity");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(identityDao, times(1))
                .save(challengeCaptor.capture());
        assertThat(challengeCaptor.getValue().getChallengeStatus())
                .isEqualTo(com.rbkmoney.fistful.magista.domain.enums.ChallengeStatus.completed);
    }

    private Change getIdentityChallenge(String expected) {
        return Change.identity_challenge(
                new ChallengeChange("id", ChallengeChangePayload.created(new Challenge(expected))));
    }

    private ChallengeChangePayload getApprovedStatus() {
        return ChallengeChangePayload
                .status_changed(ChallengeStatus.completed(new ChallengeCompleted(ChallengeResolution.approved)));
    }
}
