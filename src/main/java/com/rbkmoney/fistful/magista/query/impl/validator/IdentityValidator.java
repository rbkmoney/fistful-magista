package com.rbkmoney.fistful.magista.query.impl.validator;

import com.rbkmoney.fistful.magista.query.impl.parameters.IdentityParameters;
import com.rbkmoney.magista.dsl.PagedBaseFunction;
import com.rbkmoney.magista.dsl.QueryParameters;

public class IdentityValidator extends PagedBaseFunction.PagedBaseValidator {

    @Override
    public void validateParameters(QueryParameters queryParameters) throws IllegalArgumentException {
        super.validateParameters(queryParameters);
        IdentityParameters parameters = super.checkParamsType(queryParameters, IdentityParameters.class);

        if (parameters.getFromTime() != null && parameters.getToTime() != null) {
            validateTimePeriod(parameters.getFromTime(), parameters.getToTime());
        }
    }
}
