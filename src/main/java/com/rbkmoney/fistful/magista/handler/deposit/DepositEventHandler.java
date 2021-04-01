package com.rbkmoney.fistful.magista.handler.deposit;

import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.magista.domain.enums.DepositAdjustmentDataEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositRevertDataEventType;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositAdjustmentData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.DepositRevertData;
import com.rbkmoney.fistful.magista.handler.EventHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

import java.time.LocalDateTime;

public interface DepositEventHandler extends EventHandler<TimestampedChange, MachineEvent> {

    default void initEventFields(
            DepositData depositData,
            long eventId,
            LocalDateTime eventCreatedAt,
            LocalDateTime eventOccuredAt,
            DepositEventType eventType) {
        depositData.setId(null);
        depositData.setWtime(null);
        depositData.setEventId(eventId);
        depositData.setEventCreatedAt(eventCreatedAt);
        depositData.setEventOccuredAt(eventOccuredAt);
        depositData.setEventType(eventType);
    }

    default void initEventFields(
            DepositAdjustmentData depositAdjustmentData,
            long eventId,
            LocalDateTime eventCreatedAt,
            LocalDateTime eventOccuredAt,
            DepositAdjustmentDataEventType eventType) {
        depositAdjustmentData.setId(null);
        depositAdjustmentData.setWtime(null);
        depositAdjustmentData.setEventId(eventId);
        depositAdjustmentData.setEventCreatedAt(eventCreatedAt);
        depositAdjustmentData.setEventOccuredAt(eventOccuredAt);
        depositAdjustmentData.setEventType(eventType);
    }

    default void initEventFields(
            DepositRevertData depositRevertData,
            long eventId,
            LocalDateTime eventCreatedAt,
            LocalDateTime eventOccuredAt,
            DepositRevertDataEventType eventType) {
        depositRevertData.setId(null);
        depositRevertData.setWtime(null);
        depositRevertData.setEventId(eventId);
        depositRevertData.setEventCreatedAt(eventCreatedAt);
        depositRevertData.setEventOccuredAt(eventOccuredAt);
        depositRevertData.setEventType(eventType);
    }
}
