package com.rbkmoney.fistful.magista.query.impl.parameters;

import com.rbkmoney.fistful.magista.domain.enums.DepositStatus;
import com.rbkmoney.magista.dsl.PagedBaseFunction;
import com.rbkmoney.magista.dsl.QueryParameters;

import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.fistful.magista.query.impl.Parameters.*;

public class DepositParameters extends PagedBaseFunction.PagedBaseParameters {

    private static final Map<String, DepositStatus> statusesMap = getDepositStatusMap();

    public DepositParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
        super(parameters, derivedParameters);
    }

    public DepositParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        super(parameters, derivedParameters);
    }

    public Optional<String> getDepositId() {
        return Optional.ofNullable(getStringParameter(DEPOSIT_ID_PARAM, false));
    }

    public String getIdentityId() {
        return getStringParameter(IDENTITY_ID_PARAM, false);
    }

    public Optional<String> getWalletId() {
        return Optional.ofNullable(getStringParameter(WALLET_ID_PARAM, false));
    }

    public Optional<String> getSourceId() {
        return Optional.ofNullable(getStringParameter(SOURCE_ID_PARAM, false));
    }

    public UUID getPartyId() {
        return UUID.fromString(getStringParameter(PARTY_ID_PARAM, false));
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

    public Optional<DepositStatus> getStatus() {
        String status = getStringParameter(STATUS_PARAM, false);
        if (status != null && statusesMap.keySet().contains(status)) {
            return Optional.of(statusesMap.get(status));
        }
        return Optional.empty();
    }

    public Optional<TemporalAccessor> getFromTime() {
        return Optional.ofNullable(getTimeParameter(FROM_TIME_PARAM, false));
    }

    public Optional<TemporalAccessor> getToTime() {
        return Optional.ofNullable(getTimeParameter(TO_TIME_PARAM, false));
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
