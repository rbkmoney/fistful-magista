package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.GenericDao;
import com.rbkmoney.fistful.magista.dao.impl.field.CollectionConditionField;
import com.rbkmoney.fistful.magista.dao.impl.field.ConditionField;
import com.rbkmoney.fistful.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@DependsOn("dbInitializer")
public abstract class AbstractGenericDao extends NamedParameterJdbcDaoSupport implements GenericDao {

    private final DSLContext dslContext;

    public AbstractGenericDao(DataSource dataSource) {
        setDataSource(dataSource);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES_10);
        this.dslContext = DSL.using(configuration);
    }

    protected DSLContext getDslContext() {
        return dslContext;
    }

    @Override
    public <T> T fetchOne(Query query, Class<T> type) throws DaoException {
        return fetchOne(query, type, getNamedParameterJdbcTemplate());
    }

    @Override
    public <T> T fetchOne(Query query, Class<T> type, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetchOne(query, new SingleColumnRowMapper<>(type), namedParameterJdbcTemplate);
    }

    @Override
    public <T> T fetchOne(Query query, RowMapper<T> rowMapper) throws DaoException {
        return fetchOne(query, rowMapper, getNamedParameterJdbcTemplate());
    }

    @Override
    public <T> T fetchOne(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper) throws DaoException {
        return fetchOne(namedSql, parameterSource, rowMapper, getNamedParameterJdbcTemplate());
    }

    @Override
    public <T> T fetchOne(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetchOne(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), rowMapper, namedParameterJdbcTemplate);
    }

    @Override
    public <T> T fetchOne(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    namedSql,
                    parameterSource,
                    rowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public <T> List<T> fetch(Query query, RowMapper<T> rowMapper) throws DaoException {
        return fetch(query, rowMapper, getNamedParameterJdbcTemplate());
    }

    @Override
    public <T> List<T> fetch(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper) throws DaoException {
        return fetch(namedSql, parameterSource, rowMapper, getNamedParameterJdbcTemplate());
    }

    @Override
    public <T> List<T> fetch(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetch(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), rowMapper, namedParameterJdbcTemplate);
    }

    @Override
    public <T> List<T> fetch(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            return namedParameterJdbcTemplate.query(
                    namedSql,
                    parameterSource,
                    rowMapper
            );
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void executeOne(Query query) throws DaoException {
        execute(query, 1);
    }

    @Override
    public void executeOne(String namedSql, SqlParameterSource parameterSource) throws DaoException {
        execute(namedSql, parameterSource, 1);
    }

    @Override
    public void executeOne(Query query, KeyHolder keyHolder) throws DaoException {
        executeOne(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), keyHolder);
    }

    @Override
    public void executeOne(String namedSql, SqlParameterSource parameterSource, KeyHolder keyHolder) throws DaoException {
        execute(namedSql, parameterSource, 1, keyHolder);
    }

    @Override
    public int execute(Query query) throws DaoException {
        return execute(query, -1);
    }

    @Override
    public int execute(Query query, int expectedRowsAffected) throws DaoException {
        return execute(query, expectedRowsAffected, getNamedParameterJdbcTemplate());
    }

    @Override
    public int execute(Query query, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return execute(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), expectedRowsAffected, namedParameterJdbcTemplate);
    }

    @Override
    public int execute(String namedSql) throws DaoException {
        return execute(namedSql, EmptySqlParameterSource.INSTANCE);
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource) throws DaoException {
        return execute(namedSql, parameterSource, -1);
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected) throws DaoException {
        return execute(namedSql, parameterSource, expectedRowsAffected, getNamedParameterJdbcTemplate());
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(
                    namedSql,
                    parameterSource);

            if (expectedRowsAffected != -1 && rowsAffected != expectedRowsAffected) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(namedSql, expectedRowsAffected, rowsAffected);
            }
            return rowsAffected;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public int execute(Query query, KeyHolder keyHolder) throws DaoException {
        return execute(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), -1, keyHolder);
    }

    @Override
    public int execute(Query query, int expectedRowsAffected, KeyHolder keyHolder) throws DaoException {
        return execute(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), expectedRowsAffected, getNamedParameterJdbcTemplate(), keyHolder);
    }

    @Override
    public int execute(Query query, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate, KeyHolder keyHolder) throws DaoException {
        return execute(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), expectedRowsAffected, namedParameterJdbcTemplate, keyHolder);
    }

    @Override
    public int execute(String namedSql, KeyHolder keyHolder) throws DaoException {
        return execute(namedSql, EmptySqlParameterSource.INSTANCE, keyHolder);
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource, KeyHolder keyHolder) throws DaoException {
        return execute(namedSql, parameterSource, -1, keyHolder);
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, KeyHolder keyHolder) throws DaoException {
        return execute(namedSql, parameterSource, expectedRowsAffected, getNamedParameterJdbcTemplate(), keyHolder);
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate, KeyHolder keyHolder) throws DaoException {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(
                    namedSql,
                    parameterSource,
                    keyHolder);

            if (expectedRowsAffected != -1 && rowsAffected != expectedRowsAffected) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(namedSql, expectedRowsAffected, rowsAffected);
            }
            return rowsAffected;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    protected Condition appendConditions(Condition condition, Operator operator, ConditionParameterSource conditionParameterSource) {
        for (ConditionField field : conditionParameterSource.getConditionFields()) {
            if (field.getValue() != null) {
                condition = DSL.condition(operator, condition, buildCondition(field));
            }
        }
        return condition;
    }

    private Condition buildCondition(ConditionField field) {
        if (field.getComparator() == Comparator.IN) {
            if (field instanceof CollectionConditionField) {
                return field.getField().in(((CollectionConditionField) field).getValue());
            }
        }
        return field.getField().compare(
                field.getComparator(),
                field.getValue()
        );
    }

    protected Condition appendDateTimeRangeConditions(Condition condition,
                                                      Field<LocalDateTime> field,
                                                      Optional<LocalDateTime> fromTime,
                                                      Optional<LocalDateTime> toTime) {
        if (fromTime.isPresent()) {
            condition = condition.and(field.ge(fromTime.get()));
        }

        if (toTime.isPresent()) {
            condition = condition.and(field.lt(toTime.get()));
        }
        return condition;
    }

    public SqlParameterSource toSqlParameterSource(Map<String, Param<?>> params) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        for (Map.Entry<String, Param<?>> entry : params.entrySet()) {
            Param<?> param = entry.getValue();
            if (param.getValue() instanceof LocalDateTime || param.getValue() instanceof EnumType) {
                sqlParameterSource.addValue(entry.getKey(), param.getValue(), Types.OTHER);
            } else {
                sqlParameterSource.addValue(entry.getKey(), param.getValue());
            }
        }
        return sqlParameterSource;
    }

}
