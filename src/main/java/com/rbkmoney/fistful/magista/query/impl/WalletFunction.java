package com.rbkmoney.fistful.magista.query.impl;

import com.rbkmoney.fistful.fistful_stat.StatResponse;
import com.rbkmoney.fistful.fistful_stat.StatResponseData;
import com.rbkmoney.fistful.fistful_stat.StatWallet;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.magista.dsl.*;
import com.rbkmoney.magista.dsl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.dsl.builder.QueryBuilder;
import com.rbkmoney.magista.dsl.builder.QueryBuilderException;
import com.rbkmoney.magista.dsl.parser.AbstractQueryParser;
import com.rbkmoney.magista.dsl.parser.QueryParserException;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.fistful.magista.query.impl.Parameters.*;


public class WalletFunction extends PagedBaseFunction<Map.Entry<Long, StatWallet>, StatResponse> implements CompositeQuery<Map.Entry<Long, StatWallet>, StatResponse> {

    public static final String FUNC_NAME = "wallets";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private WalletFunction(Object descriptor, QueryParameters params, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        super(descriptor, params, FUNC_NAME, continuationToken);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Map.Entry<Long, StatWallet>, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, StatWallet>, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        QueryResult<Map.Entry<Long, StatWallet>, List<Map.Entry<Long, StatWallet>>> walletsResult = (QueryResult<Map.Entry<Long, StatWallet>, List<Map.Entry<Long, StatWallet>>>) collectedResults.get(0);

        return new BaseQueryResult<>(
                () -> walletsResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.wallets(walletsResult.getDataStream()
                            .map(walletResponse -> walletResponse.getValue()).collect(Collectors.toList()));
                    StatResponse statResponse = new StatResponse(statResponseData);
                    List<Map.Entry<Long, StatWallet>> walletStats = walletsResult.getCollectedStream();
                    if (!walletsResult.getCollectedStream().isEmpty() && getQueryParameters().getSize() == walletStats.size()) {
                        statResponse.setContinuationToken(
                                TokenUtil.buildToken(
                                        getQueryParameters(),
                                        walletStats.get(walletStats.size() - 1).getKey()
                                )
                        );
                    }
                    return statResponse;
                }
        );
    }

    @Override
    public WalletParameters getQueryParameters() {
        return (WalletParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new WalletParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class WalletParameters extends PagedBaseParameters {

        public WalletParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public WalletParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getPartyId() {
            return getStringParameter(PARTY_ID_PARAM, false);
        }

        public String getIdentityId() {
            return getStringParameter(IDENTITY_ID_PARAM, false);
        }

        public String getCurrencyCode() {
            return getStringParameter(CURRENCY_CODE_PARAM, false);
        }
    }

    public static class WalletValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            super.checkParamsType(parameters, WalletParameters.class);
        }
    }

    public static class WalletParser extends AbstractQueryParser {
        private WalletValidator validator = new WalletValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            WalletParameters parameters = getValidatedParameters(funcSource, parent, WalletParameters::new, validator);

            return Stream.of(
                    new QueryPart(FUNC_NAME, parameters, parent)
            )
                    .collect(Collectors.toList());
        }

        @Override
        public boolean apply(Map source, QueryPart parent) {
            return parent != null
                    && RootQuery.RootParser.getMainDescriptor().equals(parent.getDescriptor())
                    && (source.get(FUNC_NAME) instanceof Map);
        }

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }
    }

    public static class WalletBuilder extends AbstractQueryBuilder {
        private WalletValidator validator = new WalletValidator();

        @Override
        public Query buildQuery(List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(WalletParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart, continuationToken));
            validator.validateQuery(resultQuery);
            return resultQuery;
        }

        private CompositeQuery createQuery(QueryPart queryPart, String continuationToken) {
            List<Query> queries = Arrays.asList(
                    new GetDataFunction(queryPart.getDescriptor() + ":" + GetDataFunction.FUNC_NAME, queryPart.getParameters(), continuationToken)
            );
            CompositeQuery<QueryResult, List<QueryResult>> compositeQuery = createCompositeQuery(
                    queryPart.getDescriptor(),
                    getParameters(queryPart.getParent()),
                    queries
            );
            return createWalletFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken, compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(WalletParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static WalletFunction createWalletFunction(Object descriptor, QueryParameters queryParameters, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        WalletFunction walletFunction = new WalletFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(walletFunction);
        return walletFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction<Map.Entry<Long, StatWallet>, Collection<Map.Entry<Long, StatWallet>>> {
        private static final String FUNC_NAME = WalletFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        protected FunctionQueryContext getContext(QueryContext context) {
            return super.getContext(context, FunctionQueryContext.class);
        }

        @Override
        public QueryResult<Map.Entry<Long, StatWallet>, Collection<Map.Entry<Long, StatWallet>>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            WalletParameters parameters = new WalletParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<Map.Entry<Long, StatWallet>> result = functionContext.getSearchDao().getWallets(
                        parameters,
                        getFromId(),
                        parameters.getSize()
                );
                return new BaseQueryResult<>(result::stream, () -> result);
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }

}
