package com.rbkmoney.fistful.magista.kafka.listener;

import com.rbkmoney.fistful.magista.service.WithdrawalEventService;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalEventListener {

    @Value("${kafka.retry-delay-ms}")
    private int retryDelayMs;

    private final WithdrawalEventService withdrawalEventService;

    @KafkaListener(
            autoStartup = "${kafka.topic.withdrawal.listener.enabled}",
            topics = "${kafka.topic.withdrawal.name}",
            containerFactory = "withdrawalEventListenerContainerFactory")
    public void listen(
            List<SinkEvent> batch,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) int offset,
            Acknowledgment ack) throws InterruptedException {
        log.info("Listening Withdrawal: partition={}, offset={}, batch.size()={}", partition, offset, batch.size());
        withdrawalEventService.handleEvents(batch.stream().map(SinkEvent::getEvent).collect(toList()));
        ack.acknowledge();
        log.info("Ack Withdrawal: partition={}, offset={}", partition, offset);
    }
}
