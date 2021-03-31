package com.rbkmoney.fistful.magista.kafka.listener;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.base.CurrencyRef;
import com.rbkmoney.fistful.deposit.*;
import com.rbkmoney.fistful.deposit.adjustment.Adjustment;
import com.rbkmoney.fistful.deposit.adjustment.ChangesPlan;
import com.rbkmoney.fistful.deposit.revert.CreatedChange;
import com.rbkmoney.fistful.deposit.revert.Revert;
import com.rbkmoney.fistful.deposit.revert.status.Pending;
import com.rbkmoney.fistful.deposit.status.Status;
import com.rbkmoney.fistful.deposit.status.Succeeded;
import com.rbkmoney.fistful.magista.FistfulMagistaApplication;
import com.rbkmoney.fistful.magista.dao.DepositAdjustmentDao;
import com.rbkmoney.fistful.magista.dao.DepositDao;
import com.rbkmoney.fistful.magista.dao.DepositRevertDao;
import com.rbkmoney.fistful.magista.domain.enums.DepositAdjustmentDataStatus;
import com.rbkmoney.fistful.magista.domain.enums.DepositRevertDataStatus;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositAdjustmentData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositRevertData;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Slf4j
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = FistfulMagistaApplication.class,
        properties = {"kafka.state.cache.size=0"})
public class DepositEventListenerTest extends AbstractListenerTest {

    private static final long MESSAGE_TIMEOUT = 4_000L;

    @MockBean
    private DepositDao depositDao;

    @MockBean
    private DepositRevertDao depositRevertDao;

    @MockBean
    private DepositAdjustmentDao depositAdjustmentDao;

    @Captor
    private ArgumentCaptor<DepositData> depositDataArgumentCaptor;

    @Captor
    private ArgumentCaptor<DepositRevertData> depositRevertDataArgumentCaptor;

    @Captor
    private ArgumentCaptor<DepositAdjustmentData> depositAdjustmentDataArgumentCaptor;

    @Test
    public void shouldDepositListenAndSave() throws InterruptedException, DaoException {
        // Given
        TimestampedChange statusChanged = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.status_changed(
                        new StatusChange().setStatus(
                                Status.succeeded(new Succeeded()))));

        SinkEvent sinkEvent = sinkEvent(
                machineEvent(
                        new ThriftSerializer<>(),
                        statusChanged));

        when(depositDao.get("source_id"))
                .thenReturn(new DepositData());

        // When
        produce(sinkEvent, "mg-events-ff-deposit");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(depositDao, times(1))
                .save(depositDataArgumentCaptor.capture());
        assertThat(depositDataArgumentCaptor.getValue().getDepositStatus())
                .isEqualTo(DepositStatus.succeeded);
    }

    @Test
    public void shouldDepositRevertCreatedListenAndSave() throws InterruptedException, DaoException {
        // Given
        TimestampedChange change = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.revert(
                        new RevertChange("revert_id", com.rbkmoney.fistful.deposit.revert.Change.created(
                                new CreatedChange(
                                        new Revert("revert_id", "wallet_id", "source_id",
                                                getRevertPending(),
                                                new Cash(123L, new CurrencyRef("RUB")),
                                                "2016-03-22T06:12:27Z", 1L, 1L))))));

        SinkEvent sinkEvent = sinkEvent(
                machineEvent(
                        new ThriftSerializer<>(),
                        change));

        when(depositDao.get("source_id"))
                .thenReturn(new DepositData());

        // When
        produce(sinkEvent, "mg-events-ff-deposit");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(depositRevertDao, times(1))
                .save(depositRevertDataArgumentCaptor.capture());
        assertThat(depositRevertDataArgumentCaptor.getValue().getStatus())
                .isEqualTo(DepositRevertDataStatus.pending);
    }

    @Test
    public void shouldDepositRevertStatusListenAndSave() throws InterruptedException, DaoException {
        // Given
        TimestampedChange change = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.revert(
                        new RevertChange("revert_id", com.rbkmoney.fistful.deposit.revert.Change.status_changed(
                                new com.rbkmoney.fistful.deposit.revert.StatusChange(getRevertSucceeded())))));

        SinkEvent sinkEvent = sinkEvent(
                machineEvent(
                        new ThriftSerializer<>(),
                        change));

        when(depositRevertDao.get("source_id", "revert_id"))
                .thenReturn(new DepositRevertData());

        // When
        produce(sinkEvent, "mg-events-ff-deposit");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(depositRevertDao, times(1))
                .save(depositRevertDataArgumentCaptor.capture());
        assertThat(depositRevertDataArgumentCaptor.getValue().getStatus())
                .isEqualTo(DepositRevertDataStatus.succeeded);
    }

    @Test
    public void shouldDepositAdjustmentCreatedListenAndSave() throws InterruptedException, DaoException {
        // Given
        TimestampedChange change = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.adjustment(
                        new AdjustmentChange("adjustment_id", com.rbkmoney.fistful.deposit.adjustment.Change.created(
                                new com.rbkmoney.fistful.deposit.adjustment.CreatedChange(
                                        new Adjustment("adjustment_id", com.rbkmoney.fistful.deposit.adjustment.Status
                                                .pending(new com.rbkmoney.fistful.deposit.adjustment.Pending()),
                                                new ChangesPlan(), "2016-03-22T06:12:27Z", 1L, 1L,
                                                "2016-03-22T06:12:27Z"))))));

        SinkEvent sinkEvent = sinkEvent(
                machineEvent(
                        new ThriftSerializer<>(),
                        change));

        when(depositDao.get("source_id"))
                .thenReturn(new DepositData());

        // When
        produce(sinkEvent, "mg-events-ff-deposit");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(depositAdjustmentDao, times(1))
                .save(depositAdjustmentDataArgumentCaptor.capture());
        assertThat(depositAdjustmentDataArgumentCaptor.getValue().getStatus())
                .isEqualTo(DepositAdjustmentDataStatus.pending);
    }

    @Test
    public void shouldDepositAdjustmentStatusListenAndSave() throws InterruptedException, DaoException {
        // Given
        TimestampedChange change = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(getAdjustment());

        SinkEvent sinkEvent = sinkEvent(
                machineEvent(
                        new ThriftSerializer<>(),
                        change));

        when(depositAdjustmentDao.get("source_id", "adjustment_id"))
                .thenReturn(new DepositAdjustmentData());

        // When
        produce(sinkEvent, "mg-events-ff-deposit");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(depositAdjustmentDao, times(1))
                .save(depositAdjustmentDataArgumentCaptor.capture());
        assertThat(depositAdjustmentDataArgumentCaptor.getValue().getStatus())
                .isEqualTo(DepositAdjustmentDataStatus.succeeded);
    }

    @NotNull
    private Change getAdjustment() {
        return Change.adjustment(
                new AdjustmentChange(
                        "adjustment_id",
                        com.rbkmoney.fistful.deposit.adjustment.Change.status_changed(
                                new com.rbkmoney.fistful.deposit.adjustment.StatusChange(getAdjustmentSucceeded()))));
    }

    @NotNull
    private com.rbkmoney.fistful.deposit.adjustment.Status getAdjustmentSucceeded() {
        return com.rbkmoney.fistful.deposit.adjustment.Status
                .succeeded(new com.rbkmoney.fistful.deposit.adjustment.Succeeded());
    }

    private com.rbkmoney.fistful.deposit.revert.status.Status getRevertPending() {
        return com.rbkmoney.fistful.deposit.revert.status.Status.pending(new Pending());
    }

    private com.rbkmoney.fistful.deposit.revert.status.Status getRevertSucceeded() {
        return com.rbkmoney.fistful.deposit.revert.status.Status.succeeded(
                new com.rbkmoney.fistful.deposit.revert.status.Succeeded());
    }
}
