package io.dexproject.achatservice.validators.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);

    @Before("@annotation(LogExecution)")
    public void logBefore(JoinPoint joinPoint) {
        // Log method entry for controllers
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String parameters = (String)((Stream) Arrays.stream(args).sequential()).map(Object::toString).collect(Collectors.joining());
        logger.info("Méthode de saisie : {}.{} avec paramètres : {}", className, methodName, parameters);
    }

    @After("@annotation(LogExecution)")
    public void logAfter(JoinPoint joinPoint) {
        // Log method exit for controllers
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("Méthode de sortie : " + className + "." + methodName);
    }
}
