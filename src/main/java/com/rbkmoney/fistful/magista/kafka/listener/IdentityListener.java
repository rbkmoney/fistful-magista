package com.rbkmoney.fistful.magista.kafka.listener;

import com.rbkmoney.fistful.identity.TimestampedChange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityListener {

//    private final IdentityHandler handler;

    @KafkaListener(
            autoStartup = "${kafka.topic.identity.listener.enabled}",
            topics = "${kafka.topic.identity.name}",
            containerFactory = "identityLogListenerContainerFactory")
    public void listen(
            List<TimestampedChange> batch,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) int offset,
            Acknowledgment ack) {
        log.info("Listening Identity: partition={}, offset={}, batch.size()={}", partition, offset, batch.size());
//        handler.handle(batch);
        ack.acknowledge();
        log.info("Ack Identity: partition={}, offset={}", partition, offset);
    }
}