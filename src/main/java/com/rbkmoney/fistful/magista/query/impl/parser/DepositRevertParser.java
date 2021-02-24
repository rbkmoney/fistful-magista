package com.rbkmoney.fistful.magista.query.impl.parser;

import com.rbkmoney.fistful.magista.query.impl.DepositRevertFunction;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositRevertParameters;
import com.rbkmoney.fistful.magista.query.impl.validator.DepositRevertValidator;
import com.rbkmoney.magista.dsl.RootQuery;
import com.rbkmoney.magista.dsl.parser.AbstractQueryParser;
import com.rbkmoney.magista.dsl.parser.QueryParserException;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepositRevertParser extends AbstractQueryParser {

    private DepositRevertValidator validator = new DepositRevertValidator();

    @Override
    public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
        Map<String, Object> funcSource = (Map) source.get(DepositRevertFunction.getMainDescriptor());
        DepositRevertParameters parameters = getValidatedParameters(
                funcSource,
                parent,
                DepositRevertParameters::new,
                validator
        );

        return Stream.of(
                new QueryPart(
                        DepositRevertFunction.getMainDescriptor(),
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
                && (source.get(DepositRevertFunction.getMainDescriptor()) instanceof Map);
    }
}
