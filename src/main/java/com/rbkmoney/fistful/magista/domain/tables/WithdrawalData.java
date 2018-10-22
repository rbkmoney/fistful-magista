/*
 * This file is generated by jOOQ.
 */
package com.rbkmoney.fistful.magista.domain.tables;


import com.rbkmoney.fistful.magista.domain.Indexes;
import com.rbkmoney.fistful.magista.domain.Keys;
import com.rbkmoney.fistful.magista.domain.Mst;
import com.rbkmoney.fistful.magista.domain.tables.records.WithdrawalDataRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@Generated(
        value = {
                "http://www.jooq.org",
                "jOOQ version:3.11.5"
        },
        comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class WithdrawalData extends TableImpl<WithdrawalDataRecord> {

    /**
     * The reference instance of <code>mst.withdrawal_data</code>
     */
    public static final WithdrawalData WITHDRAWAL_DATA = new WithdrawalData();
    private static final long serialVersionUID = 316394458;
    /**
     * The column <code>mst.withdrawal_data.id</code>.
     */
    public final TableField<WithdrawalDataRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('mst.withdrawal_data_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");
    /**
     * The column <code>mst.withdrawal_data.withdrawal_id</code>.
     */
    public final TableField<WithdrawalDataRecord, String> WITHDRAWAL_ID = createField("withdrawal_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");
    /**
     * The column <code>mst.withdrawal_data.source_id</code>.
     */
    public final TableField<WithdrawalDataRecord, String> SOURCE_ID = createField("source_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");
    /**
     * The column <code>mst.withdrawal_data.destination_id</code>.
     */
    public final TableField<WithdrawalDataRecord, String> DESTINATION_ID = createField("destination_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");
    /**
     * The column <code>mst.withdrawal_data.amount</code>.
     */
    public final TableField<WithdrawalDataRecord, Long> AMOUNT = createField("amount", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");
    /**
     * The column <code>mst.withdrawal_data.currency_code</code>.
     */
    public final TableField<WithdrawalDataRecord, String> CURRENCY_CODE = createField("currency_code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * Create a <code>mst.withdrawal_data</code> table reference
     */
    public WithdrawalData() {
        this(DSL.name("withdrawal_data"), null);
    }

    /**
     * Create an aliased <code>mst.withdrawal_data</code> table reference
     */
    public WithdrawalData(String alias) {
        this(DSL.name(alias), WITHDRAWAL_DATA);
    }

    /**
     * Create an aliased <code>mst.withdrawal_data</code> table reference
     */
    public WithdrawalData(Name alias) {
        this(alias, WITHDRAWAL_DATA);
    }

    private WithdrawalData(Name alias, Table<WithdrawalDataRecord> aliased) {
        this(alias, aliased, null);
    }

    private WithdrawalData(Name alias, Table<WithdrawalDataRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> WithdrawalData(Table<O> child, ForeignKey<O, WithdrawalDataRecord> key) {
        super(child, key, WITHDRAWAL_DATA);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WithdrawalDataRecord> getRecordType() {
        return WithdrawalDataRecord.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Mst.MST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.WITHDRAWAL_DATA_PKEY, Indexes.WITHDRAWAL_DATA_UKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<WithdrawalDataRecord, Long> getIdentity() {
        return Keys.IDENTITY_WITHDRAWAL_DATA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<WithdrawalDataRecord> getPrimaryKey() {
        return Keys.WITHDRAWAL_DATA_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<WithdrawalDataRecord>> getKeys() {
        return Arrays.<UniqueKey<WithdrawalDataRecord>>asList(Keys.WITHDRAWAL_DATA_PKEY, Keys.WITHDRAWAL_DATA_UKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WithdrawalData as(String alias) {
        return new WithdrawalData(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WithdrawalData as(Name alias) {
        return new WithdrawalData(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WithdrawalData rename(String name) {
        return new WithdrawalData(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WithdrawalData rename(Name name) {
        return new WithdrawalData(name, null);
    }
}
