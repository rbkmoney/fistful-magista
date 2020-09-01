package com.rbkmoney.fistful.magista.query.impl.data;

import com.rbkmoney.fistful.fistful_stat.StatIdentity;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.query.impl.FunctionQueryContext;
import com.rbkmoney.fistful.magista.query.impl.IdentityFunction;
import com.rbkmoney.fistful.magista.query.impl.parameters.IdentityParameters;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.dsl.*;

import java.util.Collection;
import java.util.Map;

public class IdentityDataFunction extends PagedBaseFunction<Map.Entry<Long, StatIdentity>, Collection<Map.Entry<Long, StatIdentity>>> {

    private static final String FUNC_NAME = IdentityFunction.getMainDescriptor() + "_data";

    public IdentityDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
        super(descriptor, params, FUNC_NAME, continuationToken);
    }

    protected FunctionQueryContext getContext(QueryContext context) {
        return super.getContext(context, FunctionQueryContext.class);
    }

    @Override
    public QueryResult<Map.Entry<Long, StatIdentity>, Collection<Map.Entry<Long, StatIdentity>>> execute(QueryContext context) throws QueryExecutionException {
        FunctionQueryContext functionContext = getContext(context);
        IdentityParameters parameters = new IdentityParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
        try {
            Collection<Map.Entry<Long, StatIdentity>> result = functionContext.getSearchDao().getIdentities(
                    parameters,
                    TypeUtil.toLocalDateTime(parameters.getFromTime()),
                    TypeUtil.toLocalDateTime(parameters.getToTime()),
                    getFromId().orElse(null),
                    parameters.getSize()
            );
            return new BaseQueryResult<>(result::stream, () -> result);
        } catch (DaoException e) {
            throw new QueryExecutionException(e);
        }
    }
}
