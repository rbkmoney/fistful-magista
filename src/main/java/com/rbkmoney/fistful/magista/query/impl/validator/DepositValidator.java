package com.rbkmoney.fistful.magista.query.impl.validator;

import com.rbkmoney.fistful.magista.query.impl.parameters.DepositParameters;
import com.rbkmoney.magista.dsl.PagedBaseFunction;
import com.rbkmoney.magista.dsl.QueryParameters;

import static com.rbkmoney.fistful.magista.query.impl.Parameters.STATUS_PARAM;

public class DepositValidator extends PagedBaseFunction.PagedBaseValidator {

    @Override
    public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
        super.validateParameters(parameters);
        DepositParameters depositParameters = super.checkParamsType(parameters, DepositParameters.class);

        //party
        if (depositParameters.getPartyId() == null) {
            throw new IllegalArgumentException("Parameter party_id must be set like UUID");
        }

        //time
        if (depositParameters.getFromTime().isPresent() && depositParameters.getToTime().isPresent()) {
            validateTimePeriod(depositParameters.getFromTime().get(), depositParameters.getToTime().get());
        }

        //status
        String stringStatus = parameters.getStringParameter(STATUS_PARAM, false);
        if (stringStatus != null && !depositParameters.getStatus().isPresent()) {
            throw new IllegalArgumentException("Unknown deposit status: " + stringStatus);
        }
    }
}
