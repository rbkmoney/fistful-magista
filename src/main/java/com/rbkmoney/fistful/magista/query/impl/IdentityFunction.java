package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatIdentity;
import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.fistful.fistful_stat.StatResponseData;
import com.rbkmoney.fistful.magista.query.impl.parameters.IdentityParameters;
import com.rbkmoney.magista.dsl.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IdentityFunction extends PagedBaseFunction<Map.Entry<Long, StatIdentity>, StatResponse>
        implements CompositeQuery<Map.Entry<Long, StatIdentity>, StatResponse> {

    private static final String FUNC_NAME = "identities";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    public static IdentityFunction createFunction(Object descriptor, QueryParameters queryParameters,
                                                  String continuationToken,
                                                  CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        IdentityFunction func = new IdentityFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(func);
        return func;
    }

    public static String getMainDescriptor() {
        return FUNC_NAME;
    }

    private IdentityFunction(Object descriptor, QueryParameters params, String continuationToken,
                             CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        super(descriptor, params, getMainDescriptor(), continuationToken);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Map.Entry<Long, StatIdentity>, StatResponse> execute(QueryContext context)
            throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, StatIdentity>, StatResponse> execute(QueryContext context,
                                                                            List<QueryResult> collectedResults)
            throws QueryExecutionException {
        QueryResult<Map.Entry<Long, StatIdentity>, List<Map.Entry<Long, StatIdentity>>> queryResult =
                (QueryResult<Map.Entry<Long, StatIdentity>, List<Map.Entry<Long, StatIdentity>>>) collectedResults
                        .get(0);

        return new BaseQueryResult<>(
                queryResult::getDataStream,
                () -> {
                    List<StatIdentity> identities = queryResult.getDataStream()
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    StatResponse statResponse = new StatResponse(StatResponseData.identities(identities));
                    List<Map.Entry<Long, StatIdentity>> entries = queryResult.getCollectedStream();
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
    public IdentityParameters getQueryParameters() {
        return (IdentityParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new IdentityParameters(parameters, derivedParameters);
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
