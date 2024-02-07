package io.dexproject.achatservice.config;

import io.dexproject.achatservice.security.crud.services.UserAccountService;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class SpringSecurityAuditAwareImpl implements AuditorAware<String> {

    private final UserAccountService userAccountService;

    public SpringSecurityAuditAwareImpl(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.ofNullable("root");
        }
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        userPrincipal = userAccountService.loadUserByUsername(userPrincipal.getUsername());
        if (userPrincipal != null) {
            return Optional.ofNullable(userPrincipal.getUsername());
        } else {
            return Optional.ofNullable("root");
        }
    }
}