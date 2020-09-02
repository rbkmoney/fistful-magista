package com.rbkmoney.fistful.magista.query.impl.parser;

import com.rbkmoney.fistful.magista.query.impl.WalletFunction;
import com.rbkmoney.fistful.magista.query.impl.WithdrawalFunction;
import com.rbkmoney.magista.dsl.RootQuery;
import com.rbkmoney.magista.dsl.parser.BaseQueryParser;
import com.rbkmoney.magista.dsl.parser.QueryParser;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class QueryParserImpl extends BaseQueryParser {

    public QueryParserImpl() {
        this(
                Arrays.asList(
                        new RootQuery.RootParser(),
                        new WalletFunction.WalletParser(),
                        new WithdrawalFunction.WithdrawalParser(),
                        new DepositParser(),
                        new IdentityParser()
                )
        );
    }

    public QueryParserImpl(List<QueryParser<Map<String, Object>>> parsers) {
        super(parsers);
    }

    @Override
    public boolean apply(Map source, QueryPart parent) {
        return true;
    }
}
