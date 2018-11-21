package com.rbkmoney.fistful.magista.dao;

import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeData;
import com.rbkmoney.fistful.magista.domain.tables.pojos.ChallengeEvent;
import com.rbkmoney.fistful.magista.exception.DaoException;

public interface ChallengeDao extends GenericDao {

    ChallengeData getChallengeData(String identityId, String challengeId) throws DaoException;

    long saveChallengeData(ChallengeData challengeData) throws DaoException;

    ChallengeEvent getLastChallengeEvent(String identityId, String challengeId) throws DaoException;

    long saveChallengeEvent(ChallengeEvent challengeEvent) throws DaoException;

}
