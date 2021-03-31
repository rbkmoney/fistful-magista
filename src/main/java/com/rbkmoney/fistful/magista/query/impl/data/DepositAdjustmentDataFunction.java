package com.rbkmoney.fistful.magista.query.impl.data;

import com.rbkmoney.fistful.fistful_stat.StatDepositAdjustment;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.query.impl.DepositAdjustmentFunction;
import com.rbkmoney.fistful.magista.query.impl.FunctionQueryContext;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositAdjustmentParameters;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.dsl.*;

import java.util.Collection;
import java.util.Map;

public class DepositAdjustmentDataFunction extends
        PagedBaseFunction<Map.Entry<Long, StatDepositAdjustment>, Collection<Map.Entry<Long, StatDepositAdjustment>>> {

    private static final String FUNC_NAME = DepositAdjustmentFunction.getMainDescriptor() + "_data";

    public DepositAdjustmentDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
        super(descriptor, params, FUNC_NAME, continuationToken);
    }

    protected FunctionQueryContext getContext(QueryContext context) {
        return super.getContext(context, FunctionQueryContext.class);
    }

    @Override
    public QueryResult<Map.Entry<Long, StatDepositAdjustment>,
            Collection<Map.Entry<Long, StatDepositAdjustment>>> execute(QueryContext context)
            throws QueryExecutionException {
        FunctionQueryContext functionContext = getContext(context);
        DepositAdjustmentParameters parameters =
                new DepositAdjustmentParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
        try {
            Collection<Map.Entry<Long, StatDepositAdjustment>> result =
                    functionContext.getSearchDao().getDepositAdjustments(
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
