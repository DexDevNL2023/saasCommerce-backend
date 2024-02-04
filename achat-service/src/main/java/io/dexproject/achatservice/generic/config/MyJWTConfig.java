package io.dexproject.achatservice.generic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class MyJWTConfig {
    private String secret;
    private Long expiration;
}
