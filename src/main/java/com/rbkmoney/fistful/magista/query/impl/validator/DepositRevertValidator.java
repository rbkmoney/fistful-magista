package com.rbkmoney.fistful.magista.query.impl.validator;

import com.rbkmoney.fistful.magista.query.impl.parameters.DepositRevertParameters;
import com.rbkmoney.magista.dsl.PagedBaseFunction;
import com.rbkmoney.magista.dsl.QueryParameters;

import static com.rbkmoney.fistful.magista.query.impl.Parameters.STATUS_PARAM;

public class DepositRevertValidator extends PagedBaseFunction.PagedBaseValidator {

    @Override
    public void validateParameters(QueryParameters queryParameters) throws IllegalArgumentException {
        super.validateParameters(queryParameters);
        DepositRevertParameters parameters = super.checkParamsType(queryParameters, DepositRevertParameters.class);

        //time
        if (parameters.getFromTime() != null && parameters.getToTime() != null) {
            validateTimePeriod(parameters.getFromTime(), parameters.getToTime());
        }

        //status
        String stringStatus = queryParameters.getStringParameter(STATUS_PARAM, false);
        if (stringStatus != null && parameters.getStatus().isEmpty()) {
            throw new IllegalArgumentException("Unknown deposit revert status: " + stringStatus);
        }
    }
}
