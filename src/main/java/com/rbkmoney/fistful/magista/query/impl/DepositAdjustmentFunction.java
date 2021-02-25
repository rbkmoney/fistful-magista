package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatDepositAdjustment;
import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.fistful.fistful_stat.StatResponseData;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositAdjustmentParameters;
import com.rbkmoney.magista.dsl.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DepositAdjustmentFunction extends PagedBaseFunction<Map.Entry<Long, StatDepositAdjustment>, StatResponse> implements CompositeQuery<Map.Entry<Long, StatDepositAdjustment>, StatResponse> {

    private static final String FUNC_NAME = "deposit_adjustments";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    public static DepositAdjustmentFunction createFunction(Object descriptor, QueryParameters queryParameters, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        DepositAdjustmentFunction func = new DepositAdjustmentFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(func);
        return func;
    }

    public static String getMainDescriptor() {
        return FUNC_NAME;
    }

    private DepositAdjustmentFunction(Object descriptor, QueryParameters params, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        super(descriptor, params, getMainDescriptor(), continuationToken);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Map.Entry<Long, StatDepositAdjustment>, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, StatDepositAdjustment>, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        QueryResult<Map.Entry<Long, StatDepositAdjustment>, List<Map.Entry<Long, StatDepositAdjustment>>> queryResult =
                (QueryResult<Map.Entry<Long, StatDepositAdjustment>, List<Map.Entry<Long, StatDepositAdjustment>>>) collectedResults.get(0);


        return new BaseQueryResult<>(
                queryResult::getDataStream,
                () -> {
                    List<StatDepositAdjustment> depositAdjustments = queryResult.getDataStream()
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    StatResponse statResponse = new StatResponse(StatResponseData.deposit_adjustments(depositAdjustments));
                    List<Map.Entry<Long, StatDepositAdjustment>> entries = queryResult.getCollectedStream();
                    if (!entries.isEmpty() && entries.size() == getQueryParameters().getSize()) {
                        statResponse.setContinuationToken(
                                TokenUtil.buildToken(
                                        getQueryParameters(),
                                        entries.get(entries.size() - 1).getKey()
                                )
                        );
                    }
                    return statResponse;
                }
        );
    }

    @Override
    public DepositAdjustmentParameters getQueryParameters() {
        return (DepositAdjustmentParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new DepositAdjustmentParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }
}
