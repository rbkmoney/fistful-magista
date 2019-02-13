package com.rbkmoney.fistful.magista.query.impl.parser;

import com.rbkmoney.fistful.magista.query.impl.DepositFunction;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositParameters;
import com.rbkmoney.fistful.magista.query.impl.validator.DepositValidator;
import com.rbkmoney.magista.dsl.RootQuery;
import com.rbkmoney.magista.dsl.parser.AbstractQueryParser;
import com.rbkmoney.magista.dsl.parser.QueryParserException;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepositParser extends AbstractQueryParser {

    private DepositValidator validator = new DepositValidator();

    @Override
    public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
        Map<String, Object> funcSource = (Map) source.get(DepositFunction.getMainDescriptor());
        DepositParameters parameters = getValidatedParameters(
                funcSource,
                parent,
                DepositParameters::new,
                validator
        );

        return Stream.of(
                new QueryPart(
                        DepositFunction.getMainDescriptor(),
                        parameters,
                        parent
                )
        )
                .collect(Collectors.toList());
    }

    @Override
    public boolean apply(Map source, QueryPart parent) {
        return parent != null
                && RootQuery.RootParser.getMainDescriptor().equals(parent.getDescriptor())
                && (source.get(DepositFunction.getMainDescriptor()) instanceof Map);
    }
}
