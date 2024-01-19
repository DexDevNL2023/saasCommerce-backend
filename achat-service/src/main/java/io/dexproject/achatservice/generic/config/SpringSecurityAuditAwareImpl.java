package io.dexproject.achatservice.generic.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

class SpringSecurityAuditAwareImpl implements AuditorAware<String> {

    private final UserAccountService userService;

    public SpringSecurityAuditAwareImpl(UserAccountService userService) {
        this.userService = userService;
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
        UserAccount auditUser = userService.loadUserByUserEmail(userPrincipal.getUsername());
        if (auditUser != null) {
            return Optional.ofNullable(auditUser.getEmail());
        } else {
            return Optional.ofNullable("root");
        }
    }
}
