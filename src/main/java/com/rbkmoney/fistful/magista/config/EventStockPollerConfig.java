package com.rbkmoney.fistful.magista.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.FistfulServiceAdapter;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.eventstock.client.poll.ServiceAdapter;
import com.rbkmoney.fistful.magista.poller.EventStockHandler;
import com.rbkmoney.fistful.magista.service.impl.IdentityEventStockService;
import com.rbkmoney.fistful.magista.service.impl.WalletEventStockService;
import com.rbkmoney.fistful.magista.service.impl.WithdrawalEventStockService;
import com.rbkmoney.woody.api.ClientBuilder;
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
            @Value("${pooling.wallet.url}") Resource poolingUrl,
            @Value("${pooling.wallet.querySize}") int poolingQuerySize,
            @Value("${pooling.wallet.pooling.maxPoolSize}") int poolingMaxPoolSize,
            @Value("${pooling.wallet.delay}") int poolingMaxDelay
    ) throws IOException {
        return new PollingEventPublisherBuilder() {
            @Override
            protected ServiceAdapter createServiceAdapter(ClientBuilder clientBuilder) {
                return FistfulServiceAdapter.buildWalletAdapter(clientBuilder);
            }
        }
                .withURI(poolingUrl.getURI())
                .withMaxQuerySize(poolingQuerySize)
                .withMaxPoolSize(poolingMaxPoolSize)
                .withPollDelay(poolingMaxDelay)
                .withEventHandler(new EventStockHandler(walletEventStockService))
                .build();
    }

    @Bean
    public EventPublisher identityEventPublisher(
            IdentityEventStockService identityEventStockService,
            @Value("${pooling.identity.url}") Resource poolingUrl,
            @Value("${pooling.identity.querySize}") int poolingQuerySize,
            @Value("${pooling.identity.maxPoolSize}") int poolingMaxPoolSize,
            @Value("${pooling.identity.delay}") int poolingMaxDelay
    ) throws IOException {
        return new PollingEventPublisherBuilder() {
            @Override
            protected ServiceAdapter createServiceAdapter(ClientBuilder clientBuilder) {
                return FistfulServiceAdapter.buildIdentityAdapter(clientBuilder);
            }
        }
                .withURI(poolingUrl.getURI())
                .withMaxQuerySize(poolingQuerySize)
                .withMaxPoolSize(poolingMaxPoolSize)
                .withPollDelay(poolingMaxDelay)
                .withEventHandler(new EventStockHandler(identityEventStockService))
                .build();
    }

    @Bean
    public EventPublisher withdrawalEventPublisher(
            WithdrawalEventStockService withdrawalEventStockService,
            @Value("${pooling.withdrawal.url}") Resource poolingUrl,
            @Value("${pooling.withdrawal.querySize}") int poolingQuerySize,
            @Value("${pooling.withdrawal.maxPoolSize}") int poolingMaxPoolSize,
            @Value("${pooling.withdrawal.delay}") int poolingMaxDelay
    ) throws IOException {
        return new PollingEventPublisherBuilder() {
            @Override
            protected ServiceAdapter createServiceAdapter(ClientBuilder clientBuilder) {
                return FistfulServiceAdapter.buildWithdrawalAdapter(clientBuilder);
            }
        }
                .withURI(poolingUrl.getURI())
                .withMaxQuerySize(poolingQuerySize)
                .withMaxPoolSize(poolingMaxPoolSize)
                .withPollDelay(poolingMaxDelay)
                .withEventHandler(new EventStockHandler(withdrawalEventStockService))
                .build();
    }

}
