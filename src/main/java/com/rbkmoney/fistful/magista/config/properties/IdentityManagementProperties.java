package com.rbkmoney.fistful.magista.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "identity-management")
public class IdentityManagementProperties {

    private Resource url;
    private int timeout;

}
