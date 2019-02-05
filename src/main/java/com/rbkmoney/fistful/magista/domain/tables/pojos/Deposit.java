/*
 * This file is generated by jOOQ.
 */
package com.rbkmoney.fistful.magista.domain.tables.pojos;


import com.rbkmoney.fistful.magista.domain.enums.DepositEventType;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.fistful.magista.domain.enums.DepositTransferStatus;

import javax.annotation.Generated;
import java.io.Serializable;
import java.time.LocalDateTime;


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
public class Deposit implements Serializable {

    private static final long serialVersionUID = -1904826244;

    private Long id;
    private Long eventId;
    private LocalDateTime eventCreatedAt;
    private String depositId;
    private Integer sequenceId;
    private LocalDateTime eventOccuredAt;
    private DepositEventType eventType;
    private String walletId;
    private String sourceId;
    private Long amount;
    private String currencyCode;
    private DepositStatus depositStatus;
    private DepositTransferStatus depositTransferStatus;
    private Long fee;
    private Long providerFee;
    private LocalDateTime wtime;
    private Boolean current;

    public Deposit() {
    }

    public Deposit(Deposit value) {
        this.id = value.id;
        this.eventId = value.eventId;
        this.eventCreatedAt = value.eventCreatedAt;
        this.depositId = value.depositId;
        this.sequenceId = value.sequenceId;
        this.eventOccuredAt = value.eventOccuredAt;
        this.eventType = value.eventType;
        this.walletId = value.walletId;
        this.sourceId = value.sourceId;
        this.amount = value.amount;
        this.currencyCode = value.currencyCode;
        this.depositStatus = value.depositStatus;
        this.depositTransferStatus = value.depositTransferStatus;
        this.fee = value.fee;
        this.providerFee = value.providerFee;
        this.wtime = value.wtime;
        this.current = value.current;
    }

    public Deposit(
            Long id,
            Long eventId,
            LocalDateTime eventCreatedAt,
            String depositId,
            Integer sequenceId,
            LocalDateTime eventOccuredAt,
            DepositEventType eventType,
            String walletId,
            String sourceId,
            Long amount,
            String currencyCode,
            DepositStatus depositStatus,
            DepositTransferStatus depositTransferStatus,
            Long fee,
            Long providerFee,
            LocalDateTime wtime,
            Boolean current
    ) {
        this.id = id;
        this.eventId = eventId;
        this.eventCreatedAt = eventCreatedAt;
        this.depositId = depositId;
        this.sequenceId = sequenceId;
        this.eventOccuredAt = eventOccuredAt;
        this.eventType = eventType;
        this.walletId = walletId;
        this.sourceId = sourceId;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.depositStatus = depositStatus;
        this.depositTransferStatus = depositTransferStatus;
        this.fee = fee;
        this.providerFee = providerFee;
        this.wtime = wtime;
        this.current = current;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return this.eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getEventCreatedAt() {
        return this.eventCreatedAt;
    }

    public void setEventCreatedAt(LocalDateTime eventCreatedAt) {
        this.eventCreatedAt = eventCreatedAt;
    }

    public String getDepositId() {
        return this.depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    public Integer getSequenceId() {
        return this.sequenceId;
    }

    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }

    public LocalDateTime getEventOccuredAt() {
        return this.eventOccuredAt;
    }

    public void setEventOccuredAt(LocalDateTime eventOccuredAt) {
        this.eventOccuredAt = eventOccuredAt;
    }

    public DepositEventType getEventType() {
        return this.eventType;
    }

    public void setEventType(DepositEventType eventType) {
        this.eventType = eventType;
    }

    public String getWalletId() {
        return this.walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getSourceId() {
        return this.sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Long getAmount() {
        return this.amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public DepositStatus getDepositStatus() {
        return this.depositStatus;
    }

    public void setDepositStatus(DepositStatus depositStatus) {
        this.depositStatus = depositStatus;
    }

    public DepositTransferStatus getDepositTransferStatus() {
        return this.depositTransferStatus;
    }

    public void setDepositTransferStatus(DepositTransferStatus depositTransferStatus) {
        this.depositTransferStatus = depositTransferStatus;
    }

    public Long getFee() {
        return this.fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Long getProviderFee() {
        return this.providerFee;
    }

    public void setProviderFee(Long providerFee) {
        this.providerFee = providerFee;
    }

    public LocalDateTime getWtime() {
        return this.wtime;
    }

    public void setWtime(LocalDateTime wtime) {
        this.wtime = wtime;
    }

    public Boolean getCurrent() {
        return this.current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Deposit other = (Deposit) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (eventId == null) {
            if (other.eventId != null)
                return false;
        } else if (!eventId.equals(other.eventId))
            return false;
        if (eventCreatedAt == null) {
            if (other.eventCreatedAt != null)
                return false;
        } else if (!eventCreatedAt.equals(other.eventCreatedAt))
            return false;
        if (depositId == null) {
            if (other.depositId != null)
                return false;
        } else if (!depositId.equals(other.depositId))
            return false;
        if (sequenceId == null) {
            if (other.sequenceId != null)
                return false;
        } else if (!sequenceId.equals(other.sequenceId))
            return false;
        if (eventOccuredAt == null) {
            if (other.eventOccuredAt != null)
                return false;
        } else if (!eventOccuredAt.equals(other.eventOccuredAt))
            return false;
        if (eventType == null) {
            if (other.eventType != null)
                return false;
        } else if (!eventType.equals(other.eventType))
            return false;
        if (walletId == null) {
            if (other.walletId != null)
                return false;
        } else if (!walletId.equals(other.walletId))
            return false;
        if (sourceId == null) {
            if (other.sourceId != null)
                return false;
        } else if (!sourceId.equals(other.sourceId))
            return false;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (currencyCode == null) {
            if (other.currencyCode != null)
                return false;
        } else if (!currencyCode.equals(other.currencyCode))
            return false;
        if (depositStatus == null) {
            if (other.depositStatus != null)
                return false;
        } else if (!depositStatus.equals(other.depositStatus))
            return false;
        if (depositTransferStatus == null) {
            if (other.depositTransferStatus != null)
                return false;
        } else if (!depositTransferStatus.equals(other.depositTransferStatus))
            return false;
        if (fee == null) {
            if (other.fee != null)
                return false;
        } else if (!fee.equals(other.fee))
            return false;
        if (providerFee == null) {
            if (other.providerFee != null)
                return false;
        } else if (!providerFee.equals(other.providerFee))
            return false;
        if (wtime == null) {
            if (other.wtime != null)
                return false;
        } else if (!wtime.equals(other.wtime))
            return false;
        if (current == null) {
            if (other.current != null)
                return false;
        } else if (!current.equals(other.current))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.eventId == null) ? 0 : this.eventId.hashCode());
        result = prime * result + ((this.eventCreatedAt == null) ? 0 : this.eventCreatedAt.hashCode());
        result = prime * result + ((this.depositId == null) ? 0 : this.depositId.hashCode());
        result = prime * result + ((this.sequenceId == null) ? 0 : this.sequenceId.hashCode());
        result = prime * result + ((this.eventOccuredAt == null) ? 0 : this.eventOccuredAt.hashCode());
        result = prime * result + ((this.eventType == null) ? 0 : this.eventType.hashCode());
        result = prime * result + ((this.walletId == null) ? 0 : this.walletId.hashCode());
        result = prime * result + ((this.sourceId == null) ? 0 : this.sourceId.hashCode());
        result = prime * result + ((this.amount == null) ? 0 : this.amount.hashCode());
        result = prime * result + ((this.currencyCode == null) ? 0 : this.currencyCode.hashCode());
        result = prime * result + ((this.depositStatus == null) ? 0 : this.depositStatus.hashCode());
        result = prime * result + ((this.depositTransferStatus == null) ? 0 : this.depositTransferStatus.hashCode());
        result = prime * result + ((this.fee == null) ? 0 : this.fee.hashCode());
        result = prime * result + ((this.providerFee == null) ? 0 : this.providerFee.hashCode());
        result = prime * result + ((this.wtime == null) ? 0 : this.wtime.hashCode());
        result = prime * result + ((this.current == null) ? 0 : this.current.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Deposit (");

        sb.append(id);
        sb.append(", ").append(eventId);
        sb.append(", ").append(eventCreatedAt);
        sb.append(", ").append(depositId);
        sb.append(", ").append(sequenceId);
        sb.append(", ").append(eventOccuredAt);
        sb.append(", ").append(eventType);
        sb.append(", ").append(walletId);
        sb.append(", ").append(sourceId);
        sb.append(", ").append(amount);
        sb.append(", ").append(currencyCode);
        sb.append(", ").append(depositStatus);
        sb.append(", ").append(depositTransferStatus);
        sb.append(", ").append(fee);
        sb.append(", ").append(providerFee);
        sb.append(", ").append(wtime);
        sb.append(", ").append(current);

        sb.append(")");
        return sb.toString();
    }
}
