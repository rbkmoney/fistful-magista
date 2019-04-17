package com.rbkmoney.fistful.magista.dao;

import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.IdentityData;
import com.rbkmoney.fistful.magista.exception.DaoException;

public interface IdentityDao extends FistfulDao<IdentityData> {

    ChallengeData get(String identityId, String challengeId) throws DaoException;

    long save(ChallengeData challenge) throws DaoException;

}
