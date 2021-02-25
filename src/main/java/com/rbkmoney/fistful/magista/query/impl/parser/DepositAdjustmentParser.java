package com.rbkmoney.fistful.magista.query.impl.parser;

import com.rbkmoney.fistful.magista.query.impl.DepositAdjustmentFunction;
import com.rbkmoney.fistful.magista.query.impl.parameters.DepositAdjustmentParameters;
import com.rbkmoney.fistful.magista.query.impl.validator.DepositAdjustmentValidator;
import com.rbkmoney.magista.dsl.RootQuery;
import com.rbkmoney.magista.dsl.parser.AbstractQueryParser;
import com.rbkmoney.magista.dsl.parser.QueryParserException;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepositAdjustmentParser extends AbstractQueryParser {

    private DepositAdjustmentValidator validator = new DepositAdjustmentValidator();

    @Override
    public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
        Map<String, Object> funcSource = (Map) source.get(DepositAdjustmentFunction.getMainDescriptor());
        DepositAdjustmentParameters parameters = getValidatedParameters(
                funcSource,
                parent,
                DepositAdjustmentParameters::new,
                validator
        );

        return Stream.of(
                new QueryPart(
                        DepositAdjustmentFunction.getMainDescriptor(),
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
                && (source.get(DepositAdjustmentFunction.getMainDescriptor()) instanceof Map);
    }
}
