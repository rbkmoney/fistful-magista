/*
 * This file is generated by jOOQ.
 */
package com.rbkmoney.fistful.magista.domain.enums;


import com.rbkmoney.fistful.magista.domain.Mst;
import org.jooq.Catalog;
import org.jooq.EnumType;
import org.jooq.Schema;

import javax.annotation.Generated;


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
public enum DepositEventType implements EnumType {

    DEPOSIT_CREATED("DEPOSIT_CREATED"),

    DEPOSIT_STATUS_CHANGED("DEPOSIT_STATUS_CHANGED"),

    DEPOSIT_TRANSFER_CREATED("DEPOSIT_TRANSFER_CREATED"),

    DEPOSIT_TRANSFER_STATUS_CHANGED("DEPOSIT_TRANSFER_STATUS_CHANGED");

    private final String literal;

    private DepositEventType(String literal) {
        this.literal = literal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return getSchema() == null ? null : getSchema().getCatalog();
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
    public String getName() {
        return "deposit_event_type";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLiteral() {
        return literal;
    }
}
