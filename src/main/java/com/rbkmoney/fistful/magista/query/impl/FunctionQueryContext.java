package com.rbkmoney.fistful.magista.query.impl;


import com.rbkmoney.fistful.magista.dao.SearchDao;
import com.rbkmoney.magista.dsl.QueryContext;

public class FunctionQueryContext implements QueryContext {
    private final SearchDao searchDao;

    public FunctionQueryContext(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    public SearchDao getSearchDao() {
        return searchDao;
    }
}
