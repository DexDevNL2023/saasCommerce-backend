package io.dexproject.achatservice.config;

import io.dexproject.achatservice.generic.filter.dao.FilterRepo;
import io.dexproject.achatservice.generic.filter.dao.FilterRepoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories({ "io.dexproject.achatservice.repositories" })
@RequiredArgsConstructor
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditAwareImpl();
    }

    @Bean
    public FilterRepo filterRepo(){
        return new FilterRepoImpl();
    }
}
