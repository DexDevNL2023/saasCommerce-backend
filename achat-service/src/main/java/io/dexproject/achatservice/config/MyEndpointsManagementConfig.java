package io.dexproject.achatservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "management.endpoints.web.cors")
public class MyEndpointsManagementConfig {
    private String allowedMapping;
    private String[] allowedOrigins;
    private String alloweMethods;
    private String allowedHeaders;
    private Boolean allowCredentials;
    private String exposedHeaders;
    private Long maxAge;
    private String[] openApiEndpoints;
}
