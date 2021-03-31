package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatDeposit;
import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.fistful.fistful_stat.StatResponseData;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositParameters;
import com.rbkmoney.magista.dsl.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DepositFunction extends PagedBaseFunction<Map.Entry<Long, StatDeposit>, StatResponse>
        implements CompositeQuery<Map.Entry<Long, StatDeposit>, StatResponse> {

    private static final String FUNC_NAME = "deposits";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    public static DepositFunction createFunction(Object descriptor, QueryParameters queryParameters,
                                                 String continuationToken,
                                                 CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        DepositFunction func = new DepositFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(func);
        return func;
    }

    public static String getMainDescriptor() {
        return FUNC_NAME;
    }

    private DepositFunction(Object descriptor, QueryParameters params, String continuationToken,
                            CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        super(descriptor, params, getMainDescriptor(), continuationToken);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Map.Entry<Long, StatDeposit>, StatResponse> execute(QueryContext context)
            throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, StatDeposit>, StatResponse> execute(QueryContext context,
                                                                           List<QueryResult> collectedResults)
            throws QueryExecutionException {
        QueryResult<Map.Entry<Long, StatDeposit>, List<Map.Entry<Long, StatDeposit>>> queryResult =
                (QueryResult<Map.Entry<Long, StatDeposit>, List<Map.Entry<Long, StatDeposit>>>) collectedResults.get(0);

        return new BaseQueryResult<>(
                queryResult::getDataStream,
                () -> {
                    List<StatDeposit> deposits = queryResult.getDataStream()
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    StatResponse statResponse = new StatResponse(StatResponseData.deposits(deposits));
                    List<Map.Entry<Long, StatDeposit>> depositEntries = queryResult.getCollectedStream();
                    if (!depositEntries.isEmpty() && depositEntries.size() == getQueryParameters().getSize()) {
                        statResponse.setContinuationToken(
                                TokenUtil.buildToken(
                                        getQueryParameters(),
                                        depositEntries.get(depositEntries.size() - 1).getKey()
                                )
                        );
                    }
                    return statResponse;
                }
        );
    }

    @Override
    public DepositParameters getQueryParameters() {
        return (DepositParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new DepositParameters(parameters, derivedParameters);
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
