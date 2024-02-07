package io.dexproject.achatservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.paypal")
public class MyPayPalConfig {
    private String paypalClientId;
    private String paypalClientSecret;
    private String paypalMode;
}
