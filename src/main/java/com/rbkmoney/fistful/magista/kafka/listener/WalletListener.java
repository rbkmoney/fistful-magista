package com.rbkmoney.fistful.magista.kafka.listener;

import com.rbkmoney.fistful.wallet.TimestampedChange;
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
public class WalletListener {

//    private final WalletHandler handler;

    @KafkaListener(
            autoStartup = "${kafka.topic.wallet.listener.enabled}",
            topics = "${kafka.topic.wallet.name}",
            containerFactory = "walletLogListenerContainerFactory")
    public void listen(
            List<TimestampedChange> batch,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) int offset,
            Acknowledgment ack) {
        log.info("Listening Wallet: partition={}, offset={}, batch.size()={}", partition, offset, batch.size());
//        handler.handle(batch);
        ack.acknowledge();
        log.info("Ack Wallet: partition={}, offset={}", partition, offset);
    }
}