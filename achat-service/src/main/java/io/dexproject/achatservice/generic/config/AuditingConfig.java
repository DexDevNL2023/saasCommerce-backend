package io.dexproject.achatservice.generic.config;

import io.dexproject.achatservice.generic.security.crud.services.UserAccountService;
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

    private final UserAccountService userService;

    public AuditingConfig(UserAccountService userService) {
        this.userService = userService;
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditAwareImpl(userService);
    }
}
