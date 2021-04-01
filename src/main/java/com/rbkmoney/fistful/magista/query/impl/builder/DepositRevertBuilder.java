package com.rbkmoney.fistful.magista.query.impl.builder;

import com.rbkmoney.fistful.magista.query.impl.DepositRevertFunction;
import com.rbkmoney.fistful.magista.query.impl.data.DepositRevertDataFunction;
import com.rbkmoney.fistful.magista.query.impl.validator.DepositRevertValidator;
import com.rbkmoney.magista.dsl.CompositeQuery;
import com.rbkmoney.magista.dsl.Query;
import com.rbkmoney.magista.dsl.QueryResult;
import com.rbkmoney.magista.dsl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.dsl.builder.QueryBuilder;
import com.rbkmoney.magista.dsl.builder.QueryBuilderException;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.Arrays;
import java.util.List;

public class DepositRevertBuilder extends AbstractQueryBuilder {

    private final DepositRevertValidator validator = new DepositRevertValidator();

    @Override
    public Query buildQuery(
            List<QueryPart> queryParts,
            String continuationToken,
            QueryPart parentQueryPart,
            QueryBuilder baseBuilder) throws QueryBuilderException {
        Query resultQuery = buildSingleQuery(
                DepositRevertFunction.getMainDescriptor(),
                queryParts,
                queryPart -> createQuery(queryPart, continuationToken)
        );
        validator.validateQuery(resultQuery);
        return resultQuery;
    }

    private CompositeQuery createQuery(QueryPart queryPart, String continuationToken) {
        List<Query> queries = Arrays.asList(
                new DepositRevertDataFunction(
                        queryPart.getDescriptor() + ":" + DepositRevertFunction.getMainDescriptor(),
                        queryPart.getParameters(),
                        continuationToken
                )
        );
        CompositeQuery<QueryResult, List<QueryResult>> compositeQuery = createCompositeQuery(
                queryPart.getDescriptor(),
                getParameters(queryPart.getParent()),
                queries
        );
        return DepositRevertFunction
                .createFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken,
                        compositeQuery);
    }

    @Override
    public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
        return getMatchedPartsStream(DepositRevertFunction.getMainDescriptor(), queryParts)
                .findFirst()
                .isPresent();
    }
}
