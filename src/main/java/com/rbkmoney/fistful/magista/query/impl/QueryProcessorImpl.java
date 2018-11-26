package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatRequest;
import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.magista.dsl.*;
import com.rbkmoney.magista.dsl.builder.QueryBuilder;
import com.rbkmoney.magista.dsl.parser.QueryParser;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.List;

public class QueryProcessorImpl implements QueryProcessor<StatRequest, StatResponse> {
    private QueryParser<String> sourceParser;
    private QueryBuilder queryBuilder;
    private QueryContextFactory queryContextFactory;

    public QueryProcessorImpl(QueryParser<String> sourceParser, QueryBuilder queryBuilder, QueryContextFactory queryContextFactory) {
        this.sourceParser = sourceParser;
        this.queryBuilder = queryBuilder;
        this.queryContextFactory = queryContextFactory;
    }

    @Override
    public StatResponse processQuery(StatRequest source) throws BadTokenException, QueryProcessingException {
        List<QueryPart> queryParts = sourceParser.parseQuery(source.getDsl(), null);
        Query query = queryBuilder.buildQuery(queryParts, source.getContinuationToken(), null, null);
        QueryContext queryContext = queryContextFactory.getContext();
        QueryResult queryResult = query.execute(queryContext);
        Object result = queryResult.getCollectedStream();
        if (result instanceof StatResponse) {
            return (StatResponse) result;
        } else {
            throw new QueryProcessingException("QueryResult has wrong type: " + result.getClass().getName());
        }
    }
}
