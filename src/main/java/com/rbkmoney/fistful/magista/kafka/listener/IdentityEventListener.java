package com.rbkmoney.fistful.magista.kafka.listener;

import com.rbkmoney.fistful.magista.service.IdentityEventService;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class IdentityEventListener {

    private final IdentityEventService identityEventService;

    @KafkaListener(
            autoStartup = "${kafka.topic.identity.listener.enabled}",
            topics = "${kafka.topic.identity.name}",
            containerFactory = "identityEventListenerContainerFactory")
    public void listen(
            List<SinkEvent> batch,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) int offset,
            Acknowledgment ack) {
        log.info("Listening Identity: partition={}, offset={}, batch.size()={}", partition, offset, batch.size());
        identityEventService.handleEvents(batch.stream().map(SinkEvent::getEvent).collect(toList()));
        ack.acknowledge();
        log.info("Ack Identity: partition={}, offset={}", partition, offset);
    }
}