/*
 * This file is generated by jOOQ.
 */
package com.rbkmoney.fistful.magista.domain.tables.records;


import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.domain.enums.DepositTransferStatus;
import com.rbkmoney.fistful.magista.domain.tables.Deposit;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Row19;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@Generated(
        value = {
                "http://www.jooq.org",
                "jOOQ version:3.11.5"
        },
        comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class DepositRecord extends UpdatableRecordImpl<DepositRecord> implements Record19<Long, Long, LocalDateTime, String, Integer, LocalDateTime, DepositEventType, String, String, Long, String, DepositStatus, DepositTransferStatus, Long, Long, UUID, String, LocalDateTime, Boolean> {

    private static final long serialVersionUID = -419248618;

    /**
     * Setter for <code>mst.deposit.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>mst.deposit.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>mst.deposit.event_id</code>.
     */
    public void setEventId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>mst.deposit.event_id</code>.
     */
    public Long getEventId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>mst.deposit.event_created_at</code>.
     */
    public void setEventCreatedAt(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>mst.deposit.event_created_at</code>.
     */
    public LocalDateTime getEventCreatedAt() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>mst.deposit.deposit_id</code>.
     */
    public void setDepositId(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>mst.deposit.deposit_id</code>.
     */
    public String getDepositId() {
        return (String) get(3);
    }

    /**
     * Setter for <code>mst.deposit.sequence_id</code>.
     */
    public void setSequenceId(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>mst.deposit.sequence_id</code>.
     */
    public Integer getSequenceId() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>mst.deposit.event_occured_at</code>.
     */
    public void setEventOccuredAt(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>mst.deposit.event_occured_at</code>.
     */
    public LocalDateTime getEventOccuredAt() {
        return (LocalDateTime) get(5);
    }

    /**
     * Setter for <code>mst.deposit.event_type</code>.
     */
    public void setEventType(DepositEventType value) {
        set(6, value);
    }

    /**
     * Getter for <code>mst.deposit.event_type</code>.
     */
    public DepositEventType getEventType() {
        return (DepositEventType) get(6);
    }

    /**
     * Setter for <code>mst.deposit.wallet_id</code>.
     */
    public void setWalletId(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>mst.deposit.wallet_id</code>.
     */
    public String getWalletId() {
        return (String) get(7);
    }

    /**
     * Setter for <code>mst.deposit.source_id</code>.
     */
    public void setSourceId(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>mst.deposit.source_id</code>.
     */
    public String getSourceId() {
        return (String) get(8);
    }

    /**
     * Setter for <code>mst.deposit.amount</code>.
     */
    public void setAmount(Long value) {
        set(9, value);
    }

    /**
     * Getter for <code>mst.deposit.amount</code>.
     */
    public Long getAmount() {
        return (Long) get(9);
    }

    /**
     * Setter for <code>mst.deposit.currency_code</code>.
     */
    public void setCurrencyCode(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>mst.deposit.currency_code</code>.
     */
    public String getCurrencyCode() {
        return (String) get(10);
    }

    /**
     * Setter for <code>mst.deposit.deposit_status</code>.
     */
    public void setDepositStatus(DepositStatus value) {
        set(11, value);
    }

    /**
     * Getter for <code>mst.deposit.deposit_status</code>.
     */
    public DepositStatus getDepositStatus() {
        return (DepositStatus) get(11);
    }

    /**
     * Setter for <code>mst.deposit.deposit_transfer_status</code>.
     */
    public void setDepositTransferStatus(DepositTransferStatus value) {
        set(12, value);
    }

    /**
     * Getter for <code>mst.deposit.deposit_transfer_status</code>.
     */
    public DepositTransferStatus getDepositTransferStatus() {
        return (DepositTransferStatus) get(12);
    }

    /**
     * Setter for <code>mst.deposit.fee</code>.
     */
    public void setFee(Long value) {
        set(13, value);
    }

    /**
     * Getter for <code>mst.deposit.fee</code>.
     */
    public Long getFee() {
        return (Long) get(13);
    }

    /**
     * Setter for <code>mst.deposit.provider_fee</code>.
     */
    public void setProviderFee(Long value) {
        set(14, value);
    }

    /**
     * Getter for <code>mst.deposit.provider_fee</code>.
     */
    public Long getProviderFee() {
        return (Long) get(14);
    }

    /**
     * Setter for <code>mst.deposit.party_id</code>.
     */
    public void setPartyId(UUID value) {
        set(15, value);
    }

    /**
     * Getter for <code>mst.deposit.party_id</code>.
     */
    public UUID getPartyId() {
        return (UUID) get(15);
    }

    /**
     * Setter for <code>mst.deposit.identity_id</code>.
     */
    public void setIdentityId(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>mst.deposit.identity_id</code>.
     */
    public String getIdentityId() {
        return (String) get(16);
    }

    /**
     * Setter for <code>mst.deposit.wtime</code>.
     */
    public void setWtime(LocalDateTime value) {
        set(17, value);
    }

    /**
     * Getter for <code>mst.deposit.wtime</code>.
     */
    public LocalDateTime getWtime() {
        return (LocalDateTime) get(17);
    }

    /**
     * Setter for <code>mst.deposit.current</code>.
     */
    public void setCurrent(Boolean value) {
        set(18, value);
    }

    /**
     * Getter for <code>mst.deposit.current</code>.
     */
    public Boolean getCurrent() {
        return (Boolean) get(18);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record19 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Long, Long, LocalDateTime, String, Integer, LocalDateTime, DepositEventType, String, String, Long, String, DepositStatus, DepositTransferStatus, Long, Long, UUID, String, LocalDateTime, Boolean> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Long, Long, LocalDateTime, String, Integer, LocalDateTime, DepositEventType, String, String, Long, String, DepositStatus, DepositTransferStatus, Long, Long, UUID, String, LocalDateTime, Boolean> valuesRow() {
        return (Row19) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Deposit.DEPOSIT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return Deposit.DEPOSIT.EVENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field3() {
        return Deposit.DEPOSIT.EVENT_CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Deposit.DEPOSIT.DEPOSIT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return Deposit.DEPOSIT.SEQUENCE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field6() {
        return Deposit.DEPOSIT.EVENT_OCCURED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<DepositEventType> field7() {
        return Deposit.DEPOSIT.EVENT_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Deposit.DEPOSIT.WALLET_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Deposit.DEPOSIT.SOURCE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field10() {
        return Deposit.DEPOSIT.AMOUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Deposit.DEPOSIT.CURRENCY_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<DepositStatus> field12() {
        return Deposit.DEPOSIT.DEPOSIT_STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<DepositTransferStatus> field13() {
        return Deposit.DEPOSIT.DEPOSIT_TRANSFER_STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field14() {
        return Deposit.DEPOSIT.FEE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field15() {
        return Deposit.DEPOSIT.PROVIDER_FEE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UUID> field16() {
        return Deposit.DEPOSIT.PARTY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field17() {
        return Deposit.DEPOSIT.IDENTITY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field18() {
        return Deposit.DEPOSIT.WTIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field19() {
        return Deposit.DEPOSIT.CURRENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component2() {
        return getEventId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime component3() {
        return getEventCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getDepositId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getSequenceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime component6() {
        return getEventOccuredAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositEventType component7() {
        return getEventType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getWalletId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getSourceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component10() {
        return getAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component11() {
        return getCurrencyCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositStatus component12() {
        return getDepositStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositTransferStatus component13() {
        return getDepositTransferStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component14() {
        return getFee();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component15() {
        return getProviderFee();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID component16() {
        return getPartyId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component17() {
        return getIdentityId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime component18() {
        return getWtime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component19() {
        return getCurrent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value2() {
        return getEventId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value3() {
        return getEventCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getDepositId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getSequenceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value6() {
        return getEventOccuredAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositEventType value7() {
        return getEventType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getWalletId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getSourceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value10() {
        return getAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getCurrencyCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositStatus value12() {
        return getDepositStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositTransferStatus value13() {
        return getDepositTransferStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value14() {
        return getFee();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value15() {
        return getProviderFee();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID value16() {
        return getPartyId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value17() {
        return getIdentityId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value18() {
        return getWtime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value19() {
        return getCurrent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value2(Long value) {
        setEventId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value3(LocalDateTime value) {
        setEventCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value4(String value) {
        setDepositId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value5(Integer value) {
        setSequenceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value6(LocalDateTime value) {
        setEventOccuredAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value7(DepositEventType value) {
        setEventType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value8(String value) {
        setWalletId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value9(String value) {
        setSourceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value10(Long value) {
        setAmount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value11(String value) {
        setCurrencyCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value12(DepositStatus value) {
        setDepositStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value13(DepositTransferStatus value) {
        setDepositTransferStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value14(Long value) {
        setFee(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value15(Long value) {
        setProviderFee(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value16(UUID value) {
        setPartyId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value17(String value) {
        setIdentityId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value18(LocalDateTime value) {
        setWtime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord value19(Boolean value) {
        setCurrent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DepositRecord values(Long value1, Long value2, LocalDateTime value3, String value4, Integer value5, LocalDateTime value6, DepositEventType value7, String value8, String value9, Long value10, String value11, DepositStatus value12, DepositTransferStatus value13, Long value14, Long value15, UUID value16, String value17, LocalDateTime value18, Boolean value19) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DepositRecord
     */
    public DepositRecord() {
        super(Deposit.DEPOSIT);
    }

    /**
     * Create a detached, initialised DepositRecord
     */
    public DepositRecord(Long id, Long eventId, LocalDateTime eventCreatedAt, String depositId, Integer sequenceId, LocalDateTime eventOccuredAt, DepositEventType eventType, String walletId, String sourceId, Long amount, String currencyCode, DepositStatus depositStatus, DepositTransferStatus depositTransferStatus, Long fee, Long providerFee, UUID partyId, String identityId, LocalDateTime wtime, Boolean current) {
        super(Deposit.DEPOSIT);

        set(0, id);
        set(1, eventId);
        set(2, eventCreatedAt);
        set(3, depositId);
        set(4, sequenceId);
        set(5, eventOccuredAt);
        set(6, eventType);
        set(7, walletId);
        set(8, sourceId);
        set(9, amount);
        set(10, currencyCode);
        set(11, depositStatus);
        set(12, depositTransferStatus);
        set(13, fee);
        set(14, providerFee);
        set(15, partyId);
        set(16, identityId);
        set(17, wtime);
        set(18, current);
    }
}