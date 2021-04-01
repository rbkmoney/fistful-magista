package com.rbkmoney.fistful.magista.service;

import com.rbkmoney.fistful.fistful_stat.*;
import com.rbkmoney.magista.dsl.BadTokenException;
import com.rbkmoney.magista.dsl.QueryProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.thrift.TException;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class FistfulStatisticsHandler implements FistfulStatisticsSrv.Iface {

    private final QueryProcessor<StatRequest, StatResponse> queryProcessor;

    @Override
    public StatResponse getWallets(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getWithdrawals(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getDeposits(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getDestinations(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        throw new NotImplementedException("Method 'getDestinations' is not supposed to be called for this handler!");
    }

    @Override
    public StatResponse getIdentities(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getDepositReverts(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    @Override
    public StatResponse getDepositAdjustments(StatRequest statRequest) throws InvalidRequest, BadToken, TException {
        return getStatResponse(statRequest);
    }

    private StatResponse getStatResponse(StatRequest statRequest) throws InvalidRequest, BadToken {
        log.info("New stat request: {}", statRequest);
        try {
            StatResponse statResponse = queryProcessor.processQuery(statRequest);
            log.debug("Stat response: {}", statResponse);
            return statResponse;
        } catch (BadTokenException ex) {
            throw new BadToken(ex.getMessage());
        } catch (Exception e) {
            log.error("Failed to process stat request", e);
            throw new InvalidRequest(Arrays.asList(e.getMessage()));
        }
    }
}
