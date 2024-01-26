package io.dexproject.achatservice.generic.validators;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.RessourceResponse;
import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import io.dexproject.achatservice.generic.security.crud.services.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuthorizeUserAspect {

    private final UserAccountService userAccountService;

    public AuthorizeUserAspect(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Around("@annotation(AuthorizeUser)")
    public Object authorize(ProceedingJoinPoint joinPoint) throws Throwable {

        // AVANT L'EXÉCUTION DE LA MÉTHODE
        UserAccount account = userAccountService.loadCurrentUser();
        log.info("ID de l'utilisateur : " + account.getId());

        // Seul l'ID utilisateur 33 est autorisé à se connecter, les autres utilisateurs ne sont pas des utilisateurs valides.
        if (hasAuthorized(account.getRole())) {
            // écrire la logique métier de vérification d'autorisation
            log.info("L'utilisateur n'a pas l'authorisation nécésaire pour effectuer cette action : " + joinPoint.toShortString());
            return new RessourceResponse(false, "L'utilisateur n'a pas l'authorisation nécésaire pour effectuer cette action : " + joinPoint.toShortString());
        }

        // C'est là que la MÉTHODE RÉELLE sera invoquée
        Object result = joinPoint.proceed();

        // APRÈS L'EXÉCUTION DE LA MÉTHODE
        log.info(result.toString());
        return result;
    }

    private boolean hasAuthorized(RoleName authoritie) {
        return authoritie.equals(RoleName.ADMIN);
    }
}
