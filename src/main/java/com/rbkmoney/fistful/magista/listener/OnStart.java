package com.rbkmoney.fistful.magista.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.fistful.magista.service.impl.DepositEventService;
import com.rbkmoney.fistful.magista.service.impl.IdentityEventService;
import com.rbkmoney.fistful.magista.service.impl.WalletEventService;
import com.rbkmoney.fistful.magista.service.impl.WithdrawalEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {

    private final EventPublisher depositEventPublisher;
    private final EventPublisher identityEventPublisher;
    private final EventPublisher walletEventPublisher;
    private final EventPublisher withdrawalEventPublisher;

    private final DepositEventService depositEventService;
    private final IdentityEventService identityEventService;
    private final WalletEventService walletEventService;
    private final WithdrawalEventService withdrawalEventService;

    @Value("${fistful.polling.enabled:true}")
    private boolean pollingEnabled;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (pollingEnabled) {
            depositEventPublisher.subscribe(buildSubscriberConfig(depositEventService.getLastEventId()));
            identityEventPublisher.subscribe(buildSubscriberConfig(identityEventService.getLastEventId()));
            walletEventPublisher.subscribe(buildSubscriberConfig(walletEventService.getLastEventId()));
            withdrawalEventPublisher.subscribe(buildSubscriberConfig(withdrawalEventService.getLastEventId()));
        }
    }

    private SubscriberConfig buildSubscriberConfig(Optional<Long> lastEventIdOptional) {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        lastEventIdOptional.ifPresent(eventIDRange::setFromExclusive);
        EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));
        return new DefaultSubscriberConfig(eventFlowFilter);
    }
}
