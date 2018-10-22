package com.rbkmoney.fistful.magista.dao.impl;

import com.rbkmoney.fistful.magista.dao.WalletDao;
import com.rbkmoney.fistful.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.WalletEvent;
import com.rbkmoney.fistful.magista.domain.tables.records.WalletDataRecord;
import com.rbkmoney.fistful.magista.domain.tables.records.WalletEventRecord;
import com.rbkmoney.fistful.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.fistful.magista.domain.tables.WalletData.WALLET_DATA;
import static com.rbkmoney.fistful.magista.domain.tables.WalletEvent.WALLET_EVENT;

@Component
public class WalletDaoImpl extends AbstractGenericDao implements WalletDao {

    private final RecordRowMapper<WalletData> walletDataRecordRowMapper;
    private final RecordRowMapper<WalletEvent> walletEventRecordRowMapper;

    @Autowired
    public WalletDaoImpl(DataSource dataSource) {
        super(dataSource);
        walletDataRecordRowMapper = new RecordRowMapper<>(WALLET_DATA, WalletData.class);
        walletEventRecordRowMapper = new RecordRowMapper<>(WALLET_EVENT, WalletEvent.class);
    }

    @Override
    public WalletData getWalletData(String walletId) throws DaoException {
        Query query = getDslContext().selectFrom(WALLET_DATA)
                .where(WALLET_DATA.WALLET_ID.eq(walletId));

        return fetchOne(query, walletDataRecordRowMapper);
    }

    @Override
    public long saveWalletData(WalletData walletData) throws DaoException {
        WalletDataRecord walletDataRecord = getDslContext().newRecord(WALLET_DATA, walletData);

        Query query = getDslContext().insertInto(WALLET_DATA)
                .set(walletDataRecord)
                .onConflict(WALLET_DATA.WALLET_ID)
                .doNothing()
                .returning(WALLET_DATA.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public WalletEvent getLastWalletEvent(String walletId) throws DaoException {
        Query query = getDslContext()
                .selectFrom(WALLET_EVENT)
                .where(WALLET_EVENT.WALLET_ID.eq(walletId))
                .orderBy(WALLET_EVENT.ID.desc())
                .limit(1);

        return fetchOne(query, walletEventRecordRowMapper);
    }

    @Override
    public long saveWalletEvent(WalletEvent walletEvent) throws DaoException {
        WalletEventRecord walletEventRecord = getDslContext().newRecord(WALLET_EVENT, walletEvent);

        Query query = getDslContext().insertInto(WALLET_EVENT)
                .set(walletEventRecord)
                .onConflict(WALLET_EVENT.EVENT_ID, WALLET_EVENT.SEQUENCE_ID)
                .doUpdate()
                .set(walletEventRecord)
                .returning(WALLET_EVENT.ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }
}
