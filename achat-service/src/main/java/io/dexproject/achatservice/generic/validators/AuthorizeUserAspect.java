package io.dexproject.achatservice.generic.validators;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorizeUserAspect {

    @Around("@annotation(AuthorizeUser)")
    public Object authorize(ProceedingJoinPoint joinPoint) throws Throwable {

        // AVANT L'EXÉCUTION DE LA MÉTHODE
        Integer user_id = (Integer) joinPoint.getArgs()[0];
        System.out.println("ID de l'utilisateur : " + user_id);

        // Seul l'ID utilisateur 33 est autorisé à se connecter, les autres utilisateurs ne sont pas des utilisateurs valides.
        if (user_id != 33) {
            // écrire la logique métier de vérification d'autorisation
            System.out.println("Utilisateur invalide : " + user_id);
            return user_id + " est un utilisateur invalide. Veuillez vous connecter avec les informations d'identification correctes.";
        }

        // C'est là que la MÉTHODE RÉELLE sera invoquée
        Object result = joinPoint.proceed();

        // APRÈS L'EXÉCUTION DE LA MÉTHODE
        System.out.println(result);
        return result;
    }
}
