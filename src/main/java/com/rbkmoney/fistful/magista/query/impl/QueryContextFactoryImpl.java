package com.rbkmoney.fistful.magista.query.impl;


import com.rbkmoney.fistful.magista.dao.SearchDao;
import com.rbkmoney.magista.dsl.QueryContext;
import com.rbkmoney.magista.dsl.QueryContextFactory;

public class QueryContextFactoryImpl implements QueryContextFactory {
    private final SearchDao searchDao;

    public QueryContextFactoryImpl(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    @Override
    public QueryContext getContext() {
        return new FunctionQueryContext(searchDao);
    }
}
