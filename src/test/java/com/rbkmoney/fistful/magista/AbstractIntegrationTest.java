package com.rbkmoney.fistful.magista;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fistful.magista.dao.SearchDao;
import com.rbkmoney.fistful.magista.exception.DaoException;
import com.rbkmoney.fistful.magista.query.impl.QueryContextFactoryImpl;
import com.rbkmoney.fistful.magista.query.impl.QueryProcessorImpl;
import com.rbkmoney.fistful.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.fistful.magista.query.impl.parser.QueryParserImpl;
import com.rbkmoney.magista.dsl.parser.JsonQueryParser;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {"fistful.polling.enabled=false"})
@ContextConfiguration(
        classes = FistfulMagistaApplication.class,
        initializers = AbstractIntegrationTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {

    protected QueryProcessorImpl queryProcessor;

    @Autowired
    private SearchDao searchDao;

    @Value("${local.server.port}")
    protected int port;

    @ClassRule
    public static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.6")
            .withStartupTimeout(Duration.ofMinutes(5));

    @Before
    public void before() throws DaoException {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(searchDao);
        queryProcessor = new QueryProcessorImpl(
                new JsonQueryParser() {
                    @Override
                    protected ObjectMapper getMapper() {
                        ObjectMapper mapper = super.getMapper();
                        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                        return mapper;
                    }
                }
                        .withQueryParser(new QueryParserImpl()),
                new QueryBuilderImpl(),
                contextFactory
        );
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "flyway.url=" + postgres.getJdbcUrl(),
                    "flyway.user=" + postgres.getUsername(),
                    "flyway.password=" + postgres.getPassword()
            )
                    .applyTo(configurableApplicationContext);
        }
    }
}
