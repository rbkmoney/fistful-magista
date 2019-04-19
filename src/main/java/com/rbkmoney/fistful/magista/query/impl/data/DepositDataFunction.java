package com.rbkmoney.fistful.magista.query.impl.data;

import com.rbkmoney.fistful.fistful_stat.StatDeposit;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.query.impl.DepositFunction;
import com.rbkmoney.fistful.magista.query.impl.FunctionQueryContext;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositParameters;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.dsl.*;

import java.util.Collection;
import java.util.Map;

public class DepositDataFunction extends PagedBaseFunction<Map.Entry<Long, StatDeposit>, Collection<Map.Entry<Long, StatDeposit>>> {

    private static final String FUNC_NAME = DepositFunction.getMainDescriptor() + "_data";

    public DepositDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
        super(descriptor, params, FUNC_NAME, continuationToken);
    }

    protected FunctionQueryContext getContext(QueryContext context) {
        return super.getContext(context, FunctionQueryContext.class);
    }

    @Override
    public QueryResult<Map.Entry<Long, StatDeposit>, Collection<Map.Entry<Long, StatDeposit>>> execute(QueryContext context) throws QueryExecutionException {
        FunctionQueryContext functionContext = getContext(context);
        DepositParameters parameters = new DepositParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
        try {
            Collection<Map.Entry<Long, StatDeposit>> result = functionContext.getSearchDao().getDeposits(
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
