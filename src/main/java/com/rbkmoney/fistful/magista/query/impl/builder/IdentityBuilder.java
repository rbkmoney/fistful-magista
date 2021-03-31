package com.rbkmoney.fistful.magista.query.impl.builder;

import com.rbkmoney.fistful.magista.query.impl.IdentityFunction;
import com.rbkmoney.fistful.magista.query.impl.data.IdentityDataFunction;
import com.rbkmoney.fistful.magista.query.impl.validator.IdentityValidator;
import com.rbkmoney.magista.dsl.CompositeQuery;
import com.rbkmoney.magista.dsl.Query;
import com.rbkmoney.magista.dsl.QueryResult;
import com.rbkmoney.magista.dsl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.dsl.builder.QueryBuilder;
import com.rbkmoney.magista.dsl.builder.QueryBuilderException;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.Arrays;
import java.util.List;

public class IdentityBuilder extends AbstractQueryBuilder {

    private IdentityValidator validator = new IdentityValidator();

    @Override
    public Query buildQuery(List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart,
                            QueryBuilder baseBuilder) throws QueryBuilderException {
        Query resultQuery = buildSingleQuery(
                IdentityFunction.getMainDescriptor(),
                queryParts,
                queryPart -> createQuery(queryPart, continuationToken)
        );
        validator.validateQuery(resultQuery);
        return resultQuery;
    }

    private CompositeQuery createQuery(QueryPart queryPart, String continuationToken) {
        List<Query> queries = Arrays.asList(
                new IdentityDataFunction(
                        queryPart.getDescriptor() + ":" + IdentityFunction.getMainDescriptor(),
                        queryPart.getParameters(),
                        continuationToken
                )
        );
        CompositeQuery<QueryResult, List<QueryResult>> compositeQuery = createCompositeQuery(
                queryPart.getDescriptor(),
                getParameters(queryPart.getParent()),
                queries
        );
        return IdentityFunction.createFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken,
                compositeQuery);
    }

    @Override
    public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
        return getMatchedPartsStream(IdentityFunction.getMainDescriptor(), queryParts)
                .findFirst()
                .isPresent();
    }
}
