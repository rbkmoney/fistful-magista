package com.rbkmoney.fistful.magista.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fistful.fistful_stat.FistfulStatisticsSrv;
import com.rbkmoney.fistful.magista.dao.SearchDao;
import com.rbkmoney.fistful.magista.service.FistfulStatisticsHandler;
import com.rbkmoney.magista.dsl.parser.JsonQueryParser;
import com.rbkmoney.fistful.magista.query.impl.QueryContextFactoryImpl;
import com.rbkmoney.fistful.magista.query.impl.QueryProcessorImpl;
import com.rbkmoney.fistful.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.fistful.magista.query.impl.parser.QueryParserImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfig {

    @Bean
    public FistfulStatisticsSrv.Iface fistfulStatisticsHandler(
            SearchDao searchDao
            ) {
        JsonQueryParser jsonQueryParser = new JsonQueryParser() {
            @Override
            protected ObjectMapper getMapper() {
                ObjectMapper mapper = super.getMapper();
                mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                return mapper;
            }
        }.withQueryParser(new QueryParserImpl());
        return new FistfulStatisticsHandler(new QueryProcessorImpl(jsonQueryParser, new QueryBuilderImpl(), new QueryContextFactoryImpl(searchDao)));
    }
}
