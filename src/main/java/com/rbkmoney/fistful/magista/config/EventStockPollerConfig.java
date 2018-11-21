package com.rbkmoney.fistful.magista.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.FistfulPollingEventPublisherBuilder;
import com.rbkmoney.fistful.magista.poller.EventStockHandler;
import com.rbkmoney.fistful.magista.service.impl.IdentityEventStockService;
import com.rbkmoney.fistful.magista.service.impl.WalletEventStockService;
import com.rbkmoney.fistful.magista.service.impl.WithdrawalEventStockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class EventStockPollerConfig {

    @Bean
    public EventPublisher walletEventPublisher(
            WalletEventStockService walletEventStockService,
            @Value("${wallet.pooling.url}") Resource poolingUrl,
            @Value("${wallet.pooling.querySize}") int poolingQuerySize,
            @Value("${wallet.pooling.pooling.maxPoolSize}") int poolingMaxPoolSize,
            @Value("${wallet.pooling.delay}") int poolingMaxDelay,
            @Value("${wallet.pooling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWalletServiceAdapter()
                .withURI(poolingUrl.getURI())
                .withMaxQuerySize(poolingQuerySize)
                .withMaxPoolSize(poolingMaxPoolSize)
                .withPollDelay(poolingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventStockHandler(walletEventStockService))
                .build();
    }

    @Bean
    public EventPublisher identityEventPublisher(
            IdentityEventStockService identityEventStockService,
            @Value("${identity.pooling.url}") Resource poolingUrl,
            @Value("${identity.pooling.querySize}") int poolingQuerySize,
            @Value("${identity.pooling.maxPoolSize}") int poolingMaxPoolSize,
            @Value("${identity.pooling.delay}") int poolingMaxDelay,
            @Value("${identity.pooling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withIdentityServiceAdapter()
                .withURI(poolingUrl.getURI())
                .withMaxQuerySize(poolingQuerySize)
                .withMaxPoolSize(poolingMaxPoolSize)
                .withPollDelay(poolingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventStockHandler(identityEventStockService))
                .build();
    }

    @Bean
    public EventPublisher withdrawalEventPublisher(
            WithdrawalEventStockService withdrawalEventStockService,
            @Value("${withdrawal.pooling.url}") Resource poolingUrl,
            @Value("${withdrawal.pooling.querySize}") int poolingQuerySize,
            @Value("${withdrawal.pooling.maxPoolSize}") int poolingMaxPoolSize,
            @Value("${withdrawal.pooling.delay}") int poolingMaxDelay,
            @Value("${withdrawal.pooling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWithdrawalServiceAdapter()
                .withURI(poolingUrl.getURI())
                .withMaxQuerySize(poolingQuerySize)
                .withMaxPoolSize(poolingMaxPoolSize)
                .withPollDelay(poolingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventStockHandler(withdrawalEventStockService))
                .build();
    }

}
