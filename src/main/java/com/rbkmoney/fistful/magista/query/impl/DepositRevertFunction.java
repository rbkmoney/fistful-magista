package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatDepositRevert;
import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.fistful.fistful_stat.StatResponseData;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositRevertParameters;
import com.rbkmoney.magista.dsl.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DepositRevertFunction extends PagedBaseFunction<Map.Entry<Long, StatDepositRevert>, StatResponse>
        implements CompositeQuery<Map.Entry<Long, StatDepositRevert>, StatResponse> {

    private static final String FUNC_NAME = "deposit_reverts";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    public static DepositRevertFunction createFunction(
            Object descriptor,
            QueryParameters queryParameters,
            String continuationToken,
            CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        DepositRevertFunction func = new DepositRevertFunction(
                descriptor,
                queryParameters,
                continuationToken,
                subquery);
        subquery.setParentQuery(func);
        return func;
    }

    public static String getMainDescriptor() {
        return FUNC_NAME;
    }

    private DepositRevertFunction(
            Object descriptor,
            QueryParameters params,
            String continuationToken,
            CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        super(descriptor, params, getMainDescriptor(), continuationToken);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Map.Entry<Long, StatDepositRevert>, StatResponse> execute(QueryContext context)
            throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, StatDepositRevert>, StatResponse> execute(
            QueryContext context,
            List<QueryResult> collectedResults) throws QueryExecutionException {
        var queryResult = (QueryResult<Map.Entry<Long, StatDepositRevert>,
                List<Map.Entry<Long, StatDepositRevert>>>) collectedResults.get(0);

        return new BaseQueryResult<>(
                queryResult::getDataStream,
                () -> {
                    List<StatDepositRevert> depositReverts = queryResult.getDataStream()
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    StatResponse statResponse = new StatResponse(StatResponseData.deposit_reverts(depositReverts));
                    List<Map.Entry<Long, StatDepositRevert>> entries = queryResult.getCollectedStream();
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
    public DepositRevertParameters getQueryParameters() {
        return (DepositRevertParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new DepositRevertParameters(parameters, derivedParameters);
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
