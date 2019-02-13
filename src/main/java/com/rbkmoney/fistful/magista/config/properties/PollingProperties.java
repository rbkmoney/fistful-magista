package com.rbkmoney.fistful.magista.config.properties;


import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public abstract class PollingProperties {

    private Resource url;
    private int delay;
    private int retryDelay;
    private int maxPoolSize;
    private int querySize;

}
