package com.amalitech.SpringBootBloggingApp.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.amalitech.SpringBootBloggingApp.service.impl.*.*(..))")
    public void serviceImplMethods() {}

    @Before("serviceImplMethods()")
    public void logMethodCall(JoinPoint joinPoint) {
        log.info("Method {} called with arguments: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceImplMethods()", returning = "result")
    public void logMethodReturn(JoinPoint joinPoint, Object result) {
        log.info("Method {} returned with value: {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "serviceImplMethods()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Throwable exception) {
        log.error("Method {} threw an exception: {}", joinPoint.getSignature().getName(), exception.getMessage());
    }
}
