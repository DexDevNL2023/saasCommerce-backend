package io.dexproject.achatservice.config;

import io.dexproject.achatservice.security.crud.enums.RoleName;
import io.dexproject.achatservice.security.crud.services.RoleService;
import io.dexproject.achatservice.security.crud.services.UserAccountService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private final RoleService roleService;
    private final UserAccountService userAccountService;

    public SetupDataLoader(RoleService roleService, UserAccountService userAccountService) {
        this.roleService = roleService;
        this.userAccountService = userAccountService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // Create initial default role
        createRoleIfNotFound();

        // Create initial default user
        createUserIfNotFound(RoleName.ADMIN);

        alreadySetup = true;
    }

    @Transactional
    private final void createRoleIfNotFound() {
        roleService.addDefaultRoles();
    }

    @Transactional
    private final void createUserIfNotFound(final RoleName name) {
        userAccountService.addDefaultUsers(name);
    }
}