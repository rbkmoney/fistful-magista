package com.rbkmoney.fistful.magista.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "deposit.polling")
public class DepositProperties extends PollingProperties {
}
