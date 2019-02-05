package com.rbkmoney.fistful.magista.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.FistfulPollingEventPublisherBuilder;
import com.rbkmoney.fistful.magista.config.properties.DepositProperties;
import com.rbkmoney.fistful.magista.poller.EventSinkHandler;
import com.rbkmoney.fistful.magista.service.impl.DepositEventService;
import com.rbkmoney.fistful.magista.service.impl.IdentityEventService;
import com.rbkmoney.fistful.magista.service.impl.WalletEventService;
import com.rbkmoney.fistful.magista.service.impl.WithdrawalEventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class EventStockPollerConfig {

    @Bean
    public EventPublisher depositEventPublisher(DepositEventService eventService,
                                                DepositProperties depositProperties) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withDepositServiceAdapter()
                .withURI(depositProperties.getUrl().getURI())
                .withMaxQuerySize(depositProperties.getQuerySize())
                .withMaxPoolSize(depositProperties.getMaxPoolSize())
                .withPollDelay(depositProperties.getDelay())
                .withEventRetryDelay(depositProperties.getRetryDelay())
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher walletEventPublisher(
            WalletEventService walletEventService,
            @Value("${wallet.polling.url}") Resource pollingUrl,
            @Value("${wallet.polling.querySize}") int pollingQuerySize,
            @Value("${wallet.polling.maxPoolSize}") int pollingMaxPoolSize,
            @Value("${wallet.polling.delay}") int pollingMaxDelay,
            @Value("${wallet.polling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWalletServiceAdapter()
                .withURI(pollingUrl.getURI())
                .withMaxQuerySize(pollingQuerySize)
                .withMaxPoolSize(pollingMaxPoolSize)
                .withPollDelay(pollingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventSinkHandler(walletEventService))
                .build();
    }

    @Bean
    public EventPublisher identityEventPublisher(
            IdentityEventService identityEventService,
            @Value("${identity.polling.url}") Resource pollingUrl,
            @Value("${identity.polling.querySize}") int pollingQuerySize,
            @Value("${identity.polling.maxPoolSize}") int pollingMaxPoolSize,
            @Value("${identity.polling.delay}") int pollingMaxDelay,
            @Value("${identity.polling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withIdentityServiceAdapter()
                .withURI(pollingUrl.getURI())
                .withMaxQuerySize(pollingQuerySize)
                .withMaxPoolSize(pollingMaxPoolSize)
                .withPollDelay(pollingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventSinkHandler(identityEventService))
                .build();
    }

    @Bean
    public EventPublisher withdrawalEventPublisher(
            WithdrawalEventService withdrawalEventService,
            @Value("${withdrawal.polling.url}") Resource pollingUrl,
            @Value("${withdrawal.polling.querySize}") int pollingQuerySize,
            @Value("${withdrawal.polling.maxPoolSize}") int pollingMaxPoolSize,
            @Value("${withdrawal.polling.delay}") int pollingMaxDelay,
            @Value("${withdrawal.polling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWithdrawalServiceAdapter()
                .withURI(pollingUrl.getURI())
                .withMaxQuerySize(pollingQuerySize)
                .withMaxPoolSize(pollingMaxPoolSize)
                .withPollDelay(pollingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventSinkHandler(withdrawalEventService))
                .build();
    }

}
