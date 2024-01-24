package io.dexproject.achatservice.generic.config;

import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import io.dexproject.achatservice.generic.security.crud.services.UserAccountService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private final UserAccountService userAccountService;

    public SetupDataLoader(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // Create initial default user
        createUserIfNotFound(RoleName.ADMIN);

        alreadySetup = true;
    }

    @Transactional
    private final void createUserIfNotFound(final RoleName name) {
        userAccountService.addDefaultUsers(name);
    }
}