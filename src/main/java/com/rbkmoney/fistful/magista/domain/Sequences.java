/*
 * This file is generated by jOOQ.
 */
package com.rbkmoney.fistful.magista.domain;


import org.jooq.Sequence;
import org.jooq.impl.SequenceImpl;

import javax.annotation.Generated;


/**
 * Convenience access to all sequences in mst
 */
@Generated(
        value = {
                "http://www.jooq.org",
                "jOOQ version:3.11.5"
        },
        comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Sequences {

    /**
     * The sequence <code>mst.challenge_data_id_seq</code>
     */
    public static final Sequence<Long> CHALLENGE_DATA_ID_SEQ = new SequenceImpl<Long>("challenge_data_id_seq", Mst.MST, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>mst.challenge_event_id_seq</code>
     */
    public static final Sequence<Long> CHALLENGE_EVENT_ID_SEQ = new SequenceImpl<Long>("challenge_event_id_seq", Mst.MST, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>mst.identity_data_id_seq</code>
     */
    public static final Sequence<Long> IDENTITY_DATA_ID_SEQ = new SequenceImpl<Long>("identity_data_id_seq", Mst.MST, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>mst.identity_event_id_seq</code>
     */
    public static final Sequence<Long> IDENTITY_EVENT_ID_SEQ = new SequenceImpl<Long>("identity_event_id_seq", Mst.MST, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>mst.wallet_data_id_seq</code>
     */
    public static final Sequence<Long> WALLET_DATA_ID_SEQ = new SequenceImpl<Long>("wallet_data_id_seq", Mst.MST, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>mst.wallet_event_id_seq</code>
     */
    public static final Sequence<Long> WALLET_EVENT_ID_SEQ = new SequenceImpl<Long>("wallet_event_id_seq", Mst.MST, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>mst.withdrawal_data_id_seq</code>
     */
    public static final Sequence<Long> WITHDRAWAL_DATA_ID_SEQ = new SequenceImpl<Long>("withdrawal_data_id_seq", Mst.MST, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>mst.withdrawal_event_id_seq</code>
     */
    public static final Sequence<Long> WITHDRAWAL_EVENT_ID_SEQ = new SequenceImpl<Long>("withdrawal_event_id_seq", Mst.MST, org.jooq.impl.SQLDataType.BIGINT.nullable(false));
}
