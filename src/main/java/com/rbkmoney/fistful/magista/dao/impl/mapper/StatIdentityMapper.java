package com.rbkmoney.fistful.magista.dao.impl.mapper;

import com.rbkmoney.fistful.fistful_stat.StatIdentity;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import static com.rbkmoney.fistful.magista.domain.tables.IdentityData.IDENTITY_DATA;

public class StatIdentityMapper implements RowMapper<Map.Entry<Long, StatIdentity>> {

    @Override
    public Map.Entry<Long, StatIdentity> mapRow(ResultSet rs, int i) throws SQLException {
        StatIdentity identity = new StatIdentity();
        identity.setId(rs.getString(IDENTITY_DATA.IDENTITY_ID.getName()));
        identity.setName(rs.getString(IDENTITY_DATA.NAME.getName()));
        identity.setCreatedAt(TypeUtil.temporalToString(rs.getObject(IDENTITY_DATA.CREATED_AT.getName(), LocalDateTime.class)));
        identity.setProvider(rs.getInt(IDENTITY_DATA.IDENTITY_PROVIDER_ID.getName()));
        identity.setIdentityClass(rs.getString(IDENTITY_DATA.IDENTITY_CLASS_ID.getName()));
        identity.setIdentityLevel(rs.getString(IDENTITY_DATA.IDENTITY_LEVEL_ID.getName()));
        identity.setEffectiveChallenge(rs.getString(IDENTITY_DATA.IDENTITY_EFFECTIVE_CHALLENGE_ID.getName()));
        identity.setIsBlocked(rs.getString(IDENTITY_DATA.BLOCKING.getName()).equals("blocked"));
        identity.setExternalId(rs.getString(IDENTITY_DATA.EXTERNAL_ID.getName()));
        return new SimpleEntry<>(rs.getLong(IDENTITY_DATA.ID.getName()), identity);
    }
}
