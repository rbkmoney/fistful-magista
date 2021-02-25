package com.rbkmoney.fistful.magista.query.impl.parameters;

import com.rbkmoney.fistful.magista.domain.enums.DepositAdjustmentDataStatus;
import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.magista.dsl.PagedBaseFunction;
import com.rbkmoney.magista.dsl.QueryParameters;

import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.fistful.magista.query.impl.Parameters.*;

public class DepositAdjustmentParameters extends PagedBaseFunction.PagedBaseParameters {

    private static final Map<String, DepositAdjustmentDataStatus> depositAdjustmentStatusMap = getDepositAdjustmentStatusMap();
    private static final Map<String, DepositStatus> depositStatusMap = getDepositStatusMap();

    public DepositAdjustmentParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
        super(parameters, derivedParameters);
    }

    public DepositAdjustmentParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        super(parameters, derivedParameters);
    }

    public UUID getPartyId() {
        return Optional.ofNullable(getStringParameter(PARTY_ID_PARAM, false))
                .map(UUID::fromString)
                .orElse(null);
    }

    public Optional<String> getIdentityId() {
        return Optional.ofNullable(getStringParameter(IDENTITY_ID_PARAM, false));
    }

    public Optional<String> getSourceId() {
        return Optional.ofNullable(getStringParameter(SOURCE_ID_PARAM, false));
    }

    public Optional<String> getWalletId() {
        return Optional.ofNullable(getStringParameter(WALLET_ID_PARAM, false));
    }

    public Optional<String> getDepositId() {
        return Optional.ofNullable(getStringParameter(DEPOSIT_ID_PARAM, false));
    }

    public Optional<String> getAdjustmentId() {
        return Optional.ofNullable(getStringParameter(ADJUSTMENT_ID_PARAM, false));
    }

    public Optional<Long> getAmountFrom() {
        return Optional.ofNullable(getLongParameter(AMOUNT_FROM_PARAM, false));
    }

    public Optional<Long> getAmountTo() {
        return Optional.ofNullable(getLongParameter(AMOUNT_TO_PARAM, false));
    }

    public Optional<String> getCurrencyCode() {
        return Optional.ofNullable(getStringParameter(CURRENCY_CODE_PARAM, false));
    }

    public Optional<DepositAdjustmentDataStatus> getDepositAdjustmentStatus() {
        String status = getStringParameter(STATUS_PARAM, false);
        if (status != null && depositAdjustmentStatusMap.keySet().contains(status)) {
            return Optional.of(depositAdjustmentStatusMap.get(status));
        }
        return Optional.empty();
    }

    public Optional<DepositStatus> getDepositStatus() {
        String status = getStringParameter(DEPOSIT_STATUS_PARAM, false);
        if (status != null && depositStatusMap.keySet().contains(status)) {
            return Optional.of(depositStatusMap.get(status));
        }
        return Optional.empty();
    }

    public TemporalAccessor getFromTime() {
        return getTimeParameter(FROM_TIME_PARAM, false);
    }

    public TemporalAccessor getToTime() {
        return getTimeParameter(TO_TIME_PARAM, false);
    }

    private static Map<String, DepositAdjustmentDataStatus> getDepositAdjustmentStatusMap() {
        return Collections.unmodifiableMap(
                Stream.of(
                        new AbstractMap.SimpleEntry<>("Pending", DepositAdjustmentDataStatus.pending),
                        new AbstractMap.SimpleEntry<>("Succeeded", DepositAdjustmentDataStatus.succeeded)
                )
                        .collect(
                                Collectors.toMap(
                                        AbstractMap.SimpleEntry::getKey,
                                        AbstractMap.SimpleEntry::getValue
                                )
                        )
        );
    }

    private static Map<String, DepositStatus> getDepositStatusMap() {
        return Collections.unmodifiableMap(
                Stream.of(
                        new AbstractMap.SimpleEntry<>("Pending", DepositStatus.pending),
                        new AbstractMap.SimpleEntry<>("Succeeded", DepositStatus.succeeded),
                        new AbstractMap.SimpleEntry<>("Failed", DepositStatus.failed)
                )
                        .collect(
                                Collectors.toMap(
                                        AbstractMap.SimpleEntry::getKey,
                                        AbstractMap.SimpleEntry::getValue
                                )
                        )
        );
    }
}
