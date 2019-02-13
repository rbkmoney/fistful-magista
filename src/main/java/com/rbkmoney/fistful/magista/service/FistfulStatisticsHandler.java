package com.rbkmoney.fistful.magista.service;

import com.rbkmoney.fistful.fistful_stat.*;
import com.rbkmoney.magista.dsl.BadTokenException;
import com.rbkmoney.magista.dsl.QueryProcessor;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class FistfulStatisticsHandler implements FistfulStatisticsSrv.Iface {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private QueryProcessor<StatRequest, StatResponse> queryProcessor;

    public FistfulStatisticsHandler(QueryProcessor<StatRequest, StatResponse> queryProcessor) {
        this.queryProcessor = queryProcessor;
    }

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

    private StatResponse getStatResponse(StatRequest statRequest) throws InvalidRequest, BadToken {
        log.info("New stat request: {}" ,statRequest);
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
