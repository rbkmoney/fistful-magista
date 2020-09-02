package com.rbkmoney.fistful.magista.query.impl.parser;

import com.rbkmoney.fistful.magista.query.impl.IdentityFunction;
import com.rbkmoney.fistful.magista.query.impl.parameters.IdentityParameters;
import com.rbkmoney.fistful.magista.query.impl.validator.IdentityValidator;
import com.rbkmoney.magista.dsl.RootQuery;
import com.rbkmoney.magista.dsl.parser.AbstractQueryParser;
import com.rbkmoney.magista.dsl.parser.QueryParserException;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdentityParser extends AbstractQueryParser {

    private IdentityValidator validator = new IdentityValidator();

    @Override
    public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
        Map<String, Object> funcSource = (Map) source.get(IdentityFunction.getMainDescriptor());
        IdentityParameters parameters = getValidatedParameters(
                funcSource,
                parent,
                IdentityParameters::new,
                validator
        );

        return Stream.of(
                new QueryPart(
                        IdentityFunction.getMainDescriptor(),
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
                && (source.get(IdentityFunction.getMainDescriptor()) instanceof Map);
    }
}
