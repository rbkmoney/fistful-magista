package com.rbkmoney.fistful.magista.query.impl.parameters;

import com.rbkmoney.fistful.magista.domain.enums.ChallengeResolution;
import com.rbkmoney.fistful.magista.domain.enums.ChallengeStatus;
import com.rbkmoney.magista.dsl.PagedBaseFunction;
import com.rbkmoney.magista.dsl.QueryParameters;

import java.time.temporal.TemporalAccessor;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.rbkmoney.fistful.magista.query.impl.Parameters.*;

public class IdentityParameters extends PagedBaseFunction.PagedBaseParameters {

    private static final Map<String, ChallengeStatus> CHALLENGE_STATUS_MAP = Map.of("Pending", ChallengeStatus.pending,
            "Cancelled", ChallengeStatus.cancelled,
            "Completed", ChallengeStatus.completed,
            "Failed", ChallengeStatus.failed);

    private static final Map<String, ChallengeResolution> CHALLENGE_RESOLUTION_MAP =
            Map.of("Approved", ChallengeResolution.approved,
                    "Denied", ChallengeResolution.denied);

    public IdentityParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
        super(parameters, derivedParameters);
    }

    public IdentityParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        super(parameters, derivedParameters);
    }

    public TemporalAccessor getFromTime() {
        return getTimeParameter(FROM_TIME_PARAM, false);
    }

    public TemporalAccessor getToTime() {
        return getTimeParameter(TO_TIME_PARAM, false);
    }

    public UUID getPartyId() {
        return Optional.ofNullable(getStringParameter(PARTY_ID_PARAM, false))
                .map(UUID::fromString)
                .orElse(null);
    }

    public Optional<String> getPartyContractId() {
        return Optional.ofNullable(getStringParameter(PARTY_CONTRACT_ID_PARAM, false));
    }

    public Optional<String> getIdentityId() {
        return Optional.ofNullable(getStringParameter(IDENTITY_ID_PARAM, false));
    }

    public Optional<String> getIdentityProviderId() {
        return Optional.ofNullable(getStringParameter(IDENTITY_PROVIDER_ID_PARAM, false));
    }

    public Optional<String> getIdentityClassId() {
        return Optional.ofNullable(getStringParameter(IDENTITY_CLASS_ID_PARAM, false));
    }

    public Optional<String> getIdentityEffectiveChallengeId() {
        return Optional.ofNullable(getStringParameter(IDENTITY_EFFECTIVE_CHALLENGE_ID_PARAM, false));
    }

    public Optional<String> getIdentityLevelId() {
        return Optional.ofNullable(getStringParameter(IDENTITY_LEVEL_ID_PARAM, false));
    }

    public Optional<String> getChallengeId() {
        return Optional.ofNullable(getStringParameter(CHALLENGE_ID_PARAM, false));
    }

    public Optional<String> getChallengeClassId() {
        return Optional.ofNullable(getStringParameter(CHALLENGE_CLASS_ID_PARAM, false));
    }

    public Optional<ChallengeStatus> getChallengeStatus() {
        String status = getStringParameter(CHALLENGE_STATUS_PARAM, false);
        if (status != null && CHALLENGE_STATUS_MAP.keySet().contains(status)) {
            return Optional.of(CHALLENGE_STATUS_MAP.get(status));
        }
        return Optional.empty();
    }

    public Optional<ChallengeResolution> getChallengeResolution() {
        String resolution = getStringParameter(CHALLENGE_RESOLUTION_PARAM, false);
        if (resolution != null && CHALLENGE_RESOLUTION_MAP.keySet().contains(resolution)) {
            return Optional.of(CHALLENGE_RESOLUTION_MAP.get(resolution));
        }
        return Optional.empty();
    }

    public TemporalAccessor getChallengeValidUntil() {
        return getTimeParameter(CHALLENGE_VALID_UNTIL_PARAM, false);
    }
}
