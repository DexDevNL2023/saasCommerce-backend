package io.dexproject.achatservice.generic.config;

import io.dexproject.achatservice.generic.security.crud.services.impl.UserAccountServiceImpl;
import io.dexproject.achatservice.generic.utils.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories({AppConstants.DefaultPackageName + ".repositories..*"})
public class AuditingConfig {

    private final UserAccountServiceImpl userService;

    public AuditingConfig(UserAccountServiceImpl userService) {
        this.userService = userService;
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditAwareImpl(userService);
    }
}
