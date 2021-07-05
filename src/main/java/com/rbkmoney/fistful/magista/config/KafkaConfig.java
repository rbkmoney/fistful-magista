package com.rbkmoney.fistful.magista.config;

import com.rbkmoney.fistful.magista.config.properties.KafkaSslProperties;
import com.rbkmoney.fistful.magista.kafka.serde.SinkEventDeserializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentBatchErrorHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.OffsetResetStrategy.EARLIEST;
import static org.apache.kafka.common.security.auth.SecurityProtocol.SSL;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaSslProperties.class)
public class KafkaConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.consumer.group-id}")
    private String consumerGroupId;
    @Value("${kafka.consumer.concurrency}")
    private int consumerConcurrency;
    @Value("${kafka.max-poll-records}")
    private String maxPollRecords;
    @Value("${kafka.max-poll-interval-ms}")
    private int maxPollIntervalMs;
    @Value("${kafka.max-session-timeout-ms}")
    private int maxSessionTimeoutMs;

    private final KafkaSslProperties kafkaSslProperties;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MachineEvent> depositEventListenerContainerFactory() {
        return listenerContainerFactory();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MachineEvent> identityEventListenerContainerFactory() {
        return listenerContainerFactory();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MachineEvent> walletEventListenerContainerFactory() {
        return listenerContainerFactory();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MachineEvent> withdrawalEventListenerContainerFactory() {
        return listenerContainerFactory();
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, MachineEvent> listenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MachineEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        DefaultKafkaConsumerFactory<String, MachineEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerConfig());

        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(consumerConcurrency);
        factory.setBatchErrorHandler(new SeekToCurrentBatchErrorHandler());
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

    private Map<String, Object> consumerConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SinkEventDeserializer.class);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST.name().toLowerCase());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, maxSessionTimeoutMs);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        config.putAll(sslConfig());

        return config;
    }

    private Map<String, Object> sslConfig() {
        log.info("Kafka SSL isEnabled={}", kafkaSslProperties.isEnabled());
        Map<String, Object> config = new HashMap<>();

        if (kafkaSslProperties.isEnabled()) {
            config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SSL.name());
            config.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                    new File(kafkaSslProperties.getTrustStoreLocation()).getAbsolutePath());
            config.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSslProperties.getTrustStorePassword());
            config.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, kafkaSslProperties.getKeyStoreType());
            config.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, kafkaSslProperties.getTrustStoreType());
            config.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                    new File(kafkaSslProperties.getKeyStoreLocation()).getAbsolutePath());
            config.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaSslProperties.getKeyStorePassword());
            config.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaSslProperties.getKeyPassword());
        }

        return config;
    }
}
